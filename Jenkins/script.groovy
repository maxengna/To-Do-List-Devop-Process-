
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

def updateTag() {
   echo 'Cloning K8s manifests repository'

   withCredentials([usernamePassword(
    credentialsId: 'github-token',
    usernameVariable: 'GITHUB_USER',
    passwordVariable: 'GITHUB_Token'
    )]) {

        sh """
            git clone https://${GITHUB_USER}:${GITHUB_Token}@github.com/maxengna/To-Do-List-Deploy-.git ${MANIFEST_REPO}
        """
        updateK8s()
        githubPush()
    }

}

def updateK8s() {

    echo 'Updating K8s manifests with new image tags'
    
    dir("${MANIFEST_REPO}") {
    
        sh """ 
        pwd
        ls -la argocd
        sed -i 's|${BACKEND_IMAGE}.*|${BACKEND_IMAGE}${env.BUILD_NUMBER}|g' argocd/backend-deployment.yaml
        cat argocd/backend-deployment.yaml

        sed -i 's|${FRONTEND_IMAGE}.*|${FRONTEND_IMAGE}${env.BUILD_NUMBER}|g' argocd/frontend-deployment.yaml
        cat argocd/frontend-deployment.yaml
        
        """
    }

}

def githubPush() {
    echo 'Pushing K8s manifests to github1'

    withCredentials([usernamePassword(
    credentialsId: 'github-token',
    usernameVariable: 'GITHUB_USER',
    passwordVariable: 'GITHUB_Token'
    )]) {

        sh"""
            git config --global user.email "phanupong.w2019@gmail.com"
            git config --global user.name "maxengna"
            git config --list
            git branch
            git status
            git add . || echo "No changes to commit"
            git commit -m "jenkins pipeline: update k8s manifests" || echo "No changes to commit"

            echo "$GITHUB_USER"

            echo "$GITHUB_Token"
            git ls-remote https://${GITHUB_USER}:${GITHUB_Token}@github.com/maxengna/To-Do-List-Deploy-.git
            git pull https://${GITHUB_USER}:${GITHUB_Token}@github.com/$GITHUB_USER/To-Do-List-Deploy-.git master --rebase
            git push  https://${GITHUB_USER}:${GITHUB_Token}@github.com/$GITHUB_USER/To-Do-List-Deploy-.git HEAD:master

        """
    }

}

 return this
