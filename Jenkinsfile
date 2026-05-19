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
                sh "docker build -t ${DOCKER_REPO} ."
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'docker-hub-cred',
                    usernameVariable: 'USER',
                    passwordVariable: 'PASS'
                )]) {
                    sh '''
                        echo "$PASS" | docker login -u "$USER" --password-stdin
                        docker push $DOCKER_REPO
                    '''
                }
            }
        }

        stage('Deploy to DEV') {
            steps {
                withCredentials([
                    file(
                        credentialsId: 'kubeconfig-dev',
                        variable: 'KUBECONFIG'
                    )
                ]) {
                    sh '''
                    kubectl config current-context
                    kubectl apply -f deployment-dev.yaml --validate=false --insecure-skip-tls-verify=true
                    kubectl apply -f service.yaml --validate=false --insecure-skip-tls-verify=true
                    '''
                }
            }
        }

        stage('Approval') {
            steps {
                input "Deploy to Production?"
            }
        }

        // stage('Deploy to PROD') {
        //     steps {
        //         withCredentials([
        //             file(
        //                 credentialsId: 'kubeconfig-prod',
        //                 variable: 'KUBECONFIG'
        //             )
        //         ]) {
        //             sh '''
        //             kubectl apply -f deployment-prod.yaml
        //             kubectl apply -f service.yaml
        //             '''
        //         }
        //     }
        // }

//         stage('Deploy to Kubernetes') {
//              steps {
//         withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
//             sh '''
//                 export KUBECONFIG=$KUBECONFIG

//                 kubectl version --client
//                 kubectl apply -f deployment.yaml
//                 kubectl apply -f service.yaml
//             '''
//         }
//     }
// }


    //   stage('Deploy with Ansible') {
    //         steps {
    //               withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
    //                 sh '''
    //                     export KUBECONFIG=$KUBECONFIG
    //                     ansible-playbook ansible/playbook.yaml -i ansible/inventory
    //                 '''
    //             }
    //         }
    //     }
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