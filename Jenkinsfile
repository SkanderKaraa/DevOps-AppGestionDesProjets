pipeline {
    agent any

    parameters {
        booleanParam(name: 'PUSH_IMAGE', defaultValue: true, description: 'Push des images sur Docker Hub')
    }

    environment {
        SONAR_URL      = 'http://localhost:9000'
        DOCKERHUB_USER = 'siko0711'
        IMAGE_NAME     = "${DOCKERHUB_USER}/projets-backend"
        FRONT_IMAGE    = "${DOCKERHUB_USER}/projets-frontend"
        IMAGE_TAG      = "${BUILD_NUMBER}"
    }

    stages {
        stage('1 - Git') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/SkanderKaraa/DevOps-AppGestionDesProjets.git',
                    credentialsId: 'github-creds'
            }
        }

        stage('2 - Maven compile') {
            steps {
                dir('backend') {
                    sh 'mvn -B clean compile'
                }
            }
        }

        stage('3 - SonarQube') {
            steps {
                withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
                    dir('backend') {
                        sh 'mvn -B org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751:sonar -Dsonar.host.url=$SONAR_URL -Dsonar.token=$SONAR_TOKEN'
                    }
                }
            }
        }

        stage('4 - Maven test') {
            steps {
                dir('backend') {
                    sh 'mvn -B test'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('5 - Maven package') {
            steps {
                dir('backend') {
                    sh 'mvn -B package -DskipTests'
                }
            }
        }

        stage('6 - Maven deploy') {
            steps {
                dir('backend') {
                    sh 'mvn -B deploy -DskipTests -DaltDeploymentRepository=local::file:/var/lib/jenkins/local-repo'
                }
                archiveArtifacts artifacts: 'backend/target/*.jar', fingerprint: true
            }
        }

        stage('7 - Docker image backend') {
            steps {
                dir('backend') {
                    sh 'docker build -t $IMAGE_NAME:$IMAGE_TAG -t $IMAGE_NAME:latest .'
                }
                script {
                    if (params.PUSH_IMAGE) {
                        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
                            sh 'echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin'
                            sh 'docker push $IMAGE_NAME:$IMAGE_TAG'
                            sh 'docker push $IMAGE_NAME:latest'
                        }
                    }
                }
            }
        }

        stage('7b - Docker image frontend') {
            steps {
                dir('frontend') {
                    sh 'docker build -t $FRONT_IMAGE:$IMAGE_TAG -t $FRONT_IMAGE:latest .'
                }
                script {
                    if (params.PUSH_IMAGE) {
                        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
                            sh 'echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin'
                            sh 'docker push $FRONT_IMAGE:$IMAGE_TAG'
                            sh 'docker push $FRONT_IMAGE:latest'
                        }
                    }
                }
            }
        }

        stage('8 - Docker compose up') {
            steps {
                sh 'DOCKER_IMAGE=$IMAGE_NAME:$IMAGE_TAG FRONTEND_IMAGE=$FRONT_IMAGE:$IMAGE_TAG docker compose -p projets up -d'
                sh 'docker compose -p projets ps'
            }
        }
    }
}
