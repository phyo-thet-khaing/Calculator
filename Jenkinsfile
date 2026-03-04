pipeline {
    agent any

    tools {
        maven "maven3.9"
    }

    environment {
        DOCKER_REPO = 'ptk-calculator-test'
        DOCKER_HOST_PORT = '8085'
        DOCKER_CONTAINER_PORT = '8080'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/phyo-thet-khaing/Calculator.git'
            }
        }

        // stage('Build & Test') {
        //     steps {
        //         // Run tests and generate JaCoCo
        //         sh 'mvn clean verify'
                
        //         // Safely publish test results even if there are none
        //         junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
        //     }
        // }


        stage('Unit Test'){
            steps{
                sh 'mvn test' 
            }
                post {
                            always {
                                junit 'target/surefire-reports/*.xml'
                // jacoco execPattern: 'target/jacoco.exec', classPattern: 'target/classes', sourcePattern: 'src/main/java', inclusionPattern: '**/*.class'
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

        stage('Static Code Analysis (Checkstyle + SonarQube)') 
        {
    steps {
        // Run Checkstyle
        sh 'mvn checkstyle:checkstyle'

        // Publish Checkstyle Report
        publishHTML([
            allowMissing: true,
            alwaysLinkToLastBuild: true,
            keepAll: true,
            reportDir: 'target/site',
            reportFiles: 'checkstyle.html',
            reportName: 'Checkstyle Report'
        ])

        // Run SonarQube Analysis
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

        stage('Build Jar') {
            steps {
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
                echo "Running Docker container..."
                sh """
                    docker stop  ptk-calculator-test || true
                    docker rm  ptk-calculator-test || true
                    docker run -d --name ptk-calculator-test -p ${DOCKER_HOST_PORT}:${DOCKER_CONTAINER_PORT} ${DOCKER_REPO}:${IMAGE_TAG}
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
             emailext(
                to: 'phyothetkhing2002@gmail.com',
                 subject: '✅ Build SUCCESS',
                 body: 'Build completed successfully.'
             )
        }
        failure {
            echo "❌ Pipeline failed."
             emailext(
               to: 'phyothetkhing2002@gmail.com',
                subject: '❌ Build FAILED',
                body: 'Build failed. Check logs.'
             )
        }
    }
}
