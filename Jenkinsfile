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

        stage('Unit Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('JaCoCo Report') {
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

        stage('Static Code Analysis (Checkstyle)') {
            steps {
                sh 'mvn checkstyle:checkstyle'
                publishHTML(target: [
                    reportDir: 'target/site',
                    reportFiles: 'checkstyle.html',
                    reportName: 'Checkstyle Report'
                ])
            }
        }

        stage('Build Jar') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    if (!fileExists('Dockerfile')) {
                        writeFile file: 'Dockerfile', text: '''
FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
'''
                    }

                    env.IMAGE_TAG = env.BUILD_NUMBER
                    sh "docker build -t ${DOCKER_REPO}:${IMAGE_TAG} ."
                    sh "docker tag ${DOCKER_REPO}:${IMAGE_TAG} ${DOCKER_REPO}:latest"
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                script {
                    sh '''
                        docker stop calculator-test 2>/dev/null || true
                        docker rm calculator-test 2>/dev/null || true
                    '''

                    sh """
                        docker run -d \
                          --name calculator-test \
                          -p ${DOCKER_HOST_PORT}:${DOCKER_CONTAINER_PORT} \
                          ${DOCKER_REPO}:${IMAGE_TAG}
                    """
                }
            }
        }
    }

    post {
        always {
            echo "✅ Pipeline finished."
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }

        failure {
            echo "❌ Pipeline failed!"
        }

        cleanup {
            cleanWs()
        }
    }
}
