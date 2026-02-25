
def buildImage() {
    echo 'Test if Jenkins can access Docker Daemon'
    sh 'docker version'
    sh 'docker ps'

    echo 'Building Frontend image Process'
    sh "docker build -t ${FRONTEND_IMAGE}${env.BUILD_NUMBER} ./Frontend"

    echo 'Building Backend image'
    sh "docker build -t ${BACKEND_IMAGE}${env.BUILD_NUMBER} ./Backend"

    echo 'Building Database image'
    sh "docker build -t ${DATABASE_IMAGE}${env.BUILD_NUMBER} ./Database"
}

def pushImage() {
    echo 'Pushing Images to Dockerhub Repo'

    withCredentials([usernamePassword(
        credentialsId:'Dockerhub', passwordVariable:'dockerhubPass', usernameVariable:'dockerhubUser')]) {
            sh "echo $dockerhubPass | docker login -u $dockerhubUser --password-stdin"
            sh "docker push ${FRONTEND_IMAGE}${env.BUILD_NUMBER}"
            sh "docker push ${BACKEND_IMAGE}${env.BUILD_NUMBER}"
            sh "docker push ${DATABASE_IMAGE}${env.BUILD_NUMBER}"
        }
}

def updateK8s() {
    sh "sed -i 's|${BACKEND_IMAGE}.*|${BACKEND_IMAGE}${env.BUILD_NUMBER}|g' k8s/backend-deployment.yaml"
    sh'cat k8s/backend-deployment.yaml'

    sh "sed -i 's|${FRONTEND_IMAGE}.*|${FRONTEND_IMAGE}${env.BUILD_NUMBER}|g' k8s/frontend-deployment.yaml"
    sh'cat k8s/frontend-deployment.yaml'
}

def githubPush() {
    echo 'Pushing K8s manifests to github'

    withCredentials([usernamePassword(
    credentialsId: 'github-token',
    usernameVariable: 'GITHUB_USER',
    passwordVariable: 'GITHUB_PASS'
    )]) {
    
        sh'''
            git config --global user.email "phanupong.w2019@gmail.com"
            git config --global user.name "maxengna"
            git status
            git branch
            git config --list
            git remote set-url origin https://$GITHUB_USER:$GITHUB_PASS@github.com/maxengna/To-Do-List-Devop-Process.git            git add .
            git commit -m "jenkins pipeline: update k8s manifests"
            git push origin HEAD:main
            git pull origin main
        '''
    }

}

 return this