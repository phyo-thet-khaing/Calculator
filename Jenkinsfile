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