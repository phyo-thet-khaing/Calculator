pipeline {
    agent any

    tools {
        maven "maven3.9"
    }

    environment {
        DOCKER_REPO = 'ptk-calculator-test'
        DOCKER_HOST_PORT = '8085'
        DOCKER_CONTAINER_PORT = '8080'
        TEST_PORT = '8081'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/phyo-thet-khaing/Calculator.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh "mvn clean verify -Dserver.port=${TEST_PORT}"
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Publish JaCoCo Report') {
            steps {
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Coverage'
                ])
            }
        }

        stage('Static Code Analysis') {
            steps {
                sh 'mvn checkstyle:checkstyle'

                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site',
                    reportFiles: 'checkstyle.html',
                    reportName: 'Checkstyle Report'
                ])

                withSonarQubeEnv('sonar') {
                    sh 'mvn sonar:sonar'
                }
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
                sh """
                    docker stop ptk-calculator-test || true
                    docker rm ptk-calculator-test || true
                    docker run -d --name ptk-calculator-test \
                    -p ${DOCKER_HOST_PORT}:${DOCKER_CONTAINER_PORT} \
                    ${DOCKER_REPO}:${IMAGE_TAG}
                """
            }
        }
    }

    post {
        always {
            echo "✅ Pipeline finished."
        }
    }
}
