pipeline {
    agent any

    tools {
        maven "maven3.9"
    }

    environment {
        DOCKER_REPO = 'phyothetkhaing/ptk-calculator-test'    
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


        stage('Build Project') {

        stage('Build & Test') {
            steps {
                sh "mvn clean verify"
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Publish JaCoCo Report') {

            steps {
                sh 'mvn clean package -DskipTests'
            }
        }


       

        stage('Build Docker Image') {
            steps {
                script {
                    sh """
                    docker build -t phyothetkhaing/ptk-cal:1.0 .
                    """

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

        stage('Push Docker Image') {
            steps {
                sh """

                docker push phyothetkhaing/ptk-cal:1.0
                """
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh """
                kubectl apply -f ${KUBE_DEPLOYMENT}
                kubectl apply -f ${KUBE_SERVICE}
                """

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


        success {
            echo "🎉 SUCCESS: App deployed!"
           
        }

        failure {
            echo "❌ FAILED: Check logs."
           
        }

    }
}