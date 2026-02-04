pipeline {
 agent any
 
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
  
  stage('Unit Test'){
   steps{
    sh 'mvn test'
    junit 'target/surefire-reports/*.xml'
   }
  }
  
  stage('Build Jar'){
   steps{
     sh 'mvn clean package -DskipTests'
   }
  }
  
  stage('Build Docker Image') {
            steps {
                script {
                    // Build Docker image and tag it with build number
                    def imageTag = "${env.BUILD_NUMBER}"
                    sh "docker build -t ${DOCKER_REPO}:${imageTag} ."
                    sh "docker tag ${DOCKER_REPO}:${imageTag} ${DOCKER_REPO}:latest"
                    env.IMAGE_TAG = imageTag
                }
            }
        }
        
        stage('Run Docker Container') {
        steps {
            echo 'Running container locally (port 8080)...'
            sh '''
                docker stop calculator-test || true
                docker rm calculator-test || true
                docker run -d --name calculator-test -p 8082:8080 calculator-test:v1
            '''
        }    
    }
 }
}
post {
          always {
              echo "✅ Pipeline finished."
          }
          success {
             echo "Pipeline succeeded! App running at http://localhost:${env.DOCKER_HOST_PORT}/"
          }
          failure {
              echo "Pipeline failed."
          }
      }
