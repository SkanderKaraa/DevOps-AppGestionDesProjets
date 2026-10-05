pipeline {
    agent any

    environment {
        SONAR_URL = 'http://localhost:9000'
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
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    dir('backend') {
                        sh 'mvn -B org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751:sonar -Dsonar.host.url=$SONAR_URL -Dsonar.token=$SONAR_TOKEN'
                    }
                }
            }
        }
    }
}
