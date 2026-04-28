pipeline {
    agent any

    tools {
        maven "maven3.9"
    }

    environment {
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

        

        stage('Package') {
            steps {
                sh "mvn clean package -DskipTests"
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                    docker build -t phyothetkhaing/ptk-cal:1.0 .
                """
            }
        }

        

        

        stage('Push to Docker Hub') {
        steps {
            withCredentials([usernamePassword(
                credentialsId: 'docker-hub-cred',
                usernameVariable: 'USER',
                passwordVariable: 'PASS'
            )]) {
                sh 'docker login -u $USER -p $PASS'
                sh 'docker push phyothetkhaing/ptk-cal:1.0'
            }
        }
    }

       

        stage('Deploy to Kubernetes') {
            steps {
                sh 'kubectl apply -f hazelcast.yaml --validate=false'

                sh 'kubectl rollout status deployment/hazelcast'
        withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]){
          sh 'kubectl apply -f hazelcast.yaml'
                  sh 'kubectl apply -f deployment.yaml'
                  sh 'kubectl apply -f service.yaml'
                }
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