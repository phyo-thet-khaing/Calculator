pipeline {
    agent any

    tools {
        maven "maven3.9"
    }

   environment {
        DOCKER_REPO = 'calculator-test'
        DOCKER_HOST_PORT = '8082'
        DOCKER_CONTAINER_PORT = '8080'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                   url: 'https://github.com/phyo-thet-khaing/Calculator.git'
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
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Coverage',
                    allowMissing: false,   // Required parameter
                    alwaysLinkToLastBuild: true,   // Required parameter
                    keepAll: true   // Required parameter
                ])
            }
        }

        stage("Static Code Analysis (Checkstyle)") {
            steps {
                sh 'mvn checkstyle:checkstyle'
                publishHTML([
                    reportDir: 'target/site',
                    reportFiles: 'checkstyle.html',
                    reportName: 'Checkstyle Report',
                    allowMissing: false,   // Required parameter
                    alwaysLinkToLastBuild: true,   // Required parameter
                    keepAll: true   // Required parameter
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
                echo "Running container locally (port 8081)..."
                sh """
                docker stop calculator-container || true
                docker rm calculator-container || true
                docker run -d --name calculator-container -p 8082:8080 ${DOCKER_REPO}:${env.IMAGE_TAG}
                """
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline succeeded! App running at http://localhost:${DOCKER_HOST_PORT}/"
        }
        failure {
            echo "❌ Pipeline failed."
        }
        always {
            echo "🏁 Pipeline finished."
        }
    }
}
