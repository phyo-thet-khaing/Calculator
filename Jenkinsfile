pipeline {
    agent any

    tools {
        maven "maven3.9"
    }

    environment {
        DOCKER_REPO = "calculator"
        DOCKER_CREDENTIALS_ID = "dockerhub-credentials"
        DOCKER_HOST_PORT = "8082"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/travvizzzz/calculator.git'
            }
        }

        stage('Build, Test & Coverage') {
            steps {
                // This generates JaCoCo HTML at target/site/jacoco
                sh 'mvn clean verify'
                junit 'target/surefire-reports/*.xml'
            }
        }

        stage('JaCoCo Report') {
            steps {
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Coverage'
                ])
            }
        }

        stage("Static Code Analysis (Checkstyle)") {
            steps {
                sh 'mvn checkstyle:checkstyle'
                publishHTML([
                    reportDir: 'target/site',
                    reportFiles: 'checkstyle.html',
                    reportName: 'Checkstyle Report'
                ])
            }
        }

        stage('Build Jar') {
            steps {
                // Jar build AFTER coverage
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def imageTag = "${env.BUILD_NUMBER}"
                    sh "docker build -t ${DOCKER_REPO}:${imageTag} ."
                    sh "docker tag ${DOCKER_REPO}:${imageTag} ${DOCKER_REPO}:latest"
                    env.IMAGE_TAG = imageTag
                }
            }
        }

        stage('Run Docker Container') {
    steps {
        script {
            echo 'Stopping old container if exists...'
            sh '''
                docker stop calculator-container || true
                docker rm calculator-container || true
            '''

            echo 'Starting new container...'
            sh """
                docker run -d \
                  --name calculator-container \
                  -p 8082:8080 \
                  calculator:latest
            """

            sleep 10

            sh """
                curl --retry 5 --retry-delay 5 http://localhost:8082 || true
            """
        }
    }
}

    post {
        always {
            echo "✅ Pipeline finished."
        }
        success {
            echo "🎉 Pipeline succeeded! App running at http://localhost:${env.DOCKER_HOST_PORT}/"
        }
        failure {
            echo "❌ Pipeline failed."
        }
    }
}
