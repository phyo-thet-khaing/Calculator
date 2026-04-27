pipeline {
    agent any

    tools {
        maven "maven3.9"
    }

    environment {
        DOCKER_HOST_PORT = '8085'
        DOCKER_CONTAINER_PORT = '8080'
        TEST_PORT = '8081'

        DOCKER_REPO = 'phyothetkhaing/ptk-cal:1.0'
        KUBE_DEPLOYMENT = "deployment.yaml"
        KUBE_SERVICE = "service.yaml"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                url: 'https://github.com/phyo-thet-khaing/Calculator.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh "mvn clean test"
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
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

        stage('Static Code Analysis (Checkstyle + SonarQube)') {
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
                    sh """
                    ${scannerHome}/bin/sonar-scanner \
                    -Dsonar.projectKey=calculator \
                    -Dsonar.projectName=calculator \
                    -Dsonar.sources=. \
                    -Dsonar.java.binaries=target/classes
                    """
                }
            }
        }

        stage('Build Project') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_REPO} ."
            }
        }

        stage('Login to Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'docker-hub-cred',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh """
                    echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                    """
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                sh """
                docker stop ptk-calculator-test || true
                docker rm ptk-calculator-test || true
                docker run -d --name ptk-calculator-test -p ${DOCKER_HOST_PORT}:${DOCKER_CONTAINER_PORT} ${DOCKER_REPO}
                """
            }
        }

        stage('Push Docker Image') {
            steps {
                sh "docker push ${DOCKER_REPO}"
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh """
                kubectl apply -f ${KUBE_DEPLOYMENT}
                kubectl apply -f ${KUBE_SERVICE}
                """
            }
        }
    }

    post {
        always {
            echo "✅ Pipeline finished."
        }

        success {
            echo "🎉 SUCCESS: App deployed!"
        }

        failure {
            echo "❌ FAILED: Check logs."
        }
    }
}