pipeline {
    agent any
    tools {
        maven 'Maven 3'
    }
    stages {
        stage('Build Maven') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/main']], userRemoteConfigs: [[url: 'https://github.com/ChanduRaaparthi21/Automatic-Email-Sender-Hr']]])
                bat 'mvn clean install'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    bat 'docker build -t chandu221299/emailsender .'
                }
            }
        }
        stage('Push Image to Docker Hub') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'dockerhubpwd', variable: 'DOCKER_PASSWORD')]) {
                        bat 'echo %DOCKER_PASSWORD% | docker login -u chanduraaparthi@gmail.com --password-stdin'
                    }
                    bat 'docker push chandu221299/emailsender'
                }
            }
        }
    }
}
