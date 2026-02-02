pipeline {
    agent any
    
    environment {
        DOCKER_REPO = 'calculator-test'
        DOCKER_HOST_PORT = '8080'
        DOCKER_CONTAINER_PORT = '8080'
    }
    
    stages {
        // Stage 1: Checkout code
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Ellin2024/Calculator-Test.git'
            }
        }
        
        // Stage 2: Unit Test
        stage('Unit Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        
        // Stage 3: Build JAR
        stage('Build Jar') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        
        // Stage 4: Build Docker Image
        stage('Build Docker Image') {
            steps {
                script {
                    // Create Dockerfile if it doesn't exist
                    if (!fileExists('Dockerfile')) {
                        writeFile file: 'Dockerfile', text: '''
                        FROM openjdk:11-jre-slim
                        WORKDIR /app
                        COPY target/*.jar app.jar
                        EXPOSE 8080
                        ENTRYPOINT ["java", "-jar", "app.jar"]
                        '''
                    }
                    
                    // Build Docker image and tag it with build number
                    def imageTag = "${env.BUILD_NUMBER}"
                    sh "docker build -t ${env.DOCKER_REPO}:${imageTag} ."
                    sh "docker tag ${env.DOCKER_REPO}:${imageTag} ${env.DOCKER_REPO}:latest"
                    env.IMAGE_TAG = imageTag
                }
            }
        }
        
        // Stage 5: Run Docker Container
        stage('Run Docker Container') {
            steps {
                script {
                    echo 'Stopping and removing any existing container...'
                    sh '''
                        docker stop calculator-test 2>/dev/null || true
                        docker rm calculator-test 2>/dev/null || true
                    '''
                    
                    echo 'Running new container...'
                    sh """
                        docker run -d \
                          --name calculator-test \
                          -p ${env.DOCKER_HOST_PORT}:${env.DOCKER_CONTAINER_PORT} \
                          ${env.DOCKER_REPO}:${env.IMAGE_TAG}
                    """
                    
                    // Wait for container to start
                    sleep 10
                    
                    // Health check
                    sh """
                        echo "Checking if application is running..."
                        curl --retry 5 --retry-delay 5 --max-time 30 \
                             http://localhost:${env.DOCKER_HOST_PORT} || echo "Application not responding yet"
                    """
                }
            }
        }
    }
    
    post {
        always {
            echo "✅ Pipeline finished."
            
            // Archive artifacts
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
        
        success {
            echo "🎉 Pipeline succeeded!"
            echo "🌐 App running at: http://localhost:${env.DOCKER_HOST_PORT}/"
            
            // Display container info
            script {
                sh '''
                    echo "Container Status:"
                    docker ps --filter "name=calculator-test" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
                    
                    echo ""
                    echo "Docker Images:"
                    docker images | grep calculator-test || echo "No calculator-test images found"
                '''
            }
        }
        
        failure {
            echo "❌ Pipeline failed!"
            
            // Cleanup on failure
            script {
                sh 'docker stop calculator-test 2>/dev/null || true'
                sh 'docker rm calculator-test 2>/dev/null || true'
            }
        }
        
        cleanup {
            // Always clean workspace
            cleanWs()
        }
    }
}