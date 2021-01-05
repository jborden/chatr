node {
  checkout scm

  // get the branch name
  if (env.BRANCH_NAME != null) {
    branch = env.BRANCH_NAME
  } else if (env.CHANGE_TARGET != null) {
    branch = env.CHANGE_TARGET
  } else {
    branch = null
  }

  jenkinsHost = "builds.chatr.chat"
  jenkinsJob = "chatr"
  jenkinsBuildUrl = "https://${jenkinsHost}/blue/organizations/jenkins/${jenkinsJob}/detail/${branch}/${env.BUILD_NUMBER}/pipeline"
  jenkinsConsoleUrl = "https://${jenkinsHost}/job/${jenkinsJob}/job/${branch}/${env.BUILD_NUMBER}/console"
  jenkinsSlackLinks = "(<${jenkinsBuildUrl}|Open>) (<${jenkinsConsoleUrl}|Console>)"

  def sendSlackMsgFull = {
    msg,color ->
    if (color == 'blue') {
      colorVal = '#4071bf'
    } else if (color != null) {
      colorVal = color
    } else {
      if (currentBuild.result == 'FAILURE') {
        colorVal = 'danger'
      } else if (currentBuild.result == 'UNSTABLE') {
        colorVal = 'warning'
      } else if (currentBuild.result == 'SUCCESS') {
        colorVal = 'good'
      }
    }
    msgContent = "[${env.JOB_NAME} #${env.BUILD_NUMBER}] ${msg} | ${jenkinsSlackLinks}"
    if (colorVal != null) {
      slackSend color: colorVal, message: msgContent, channel: 'datasource_logs'
    } else {
      slackSend message: msgContent, channel: 'datasource_logs'
    }
  }

  def sendSlackMsg = {
    msg ->
    sendSlackMsgFull (msg, null)
  }

  stage('Init') {
    // sendSlackMsgFull ('Build Started', 'blue')
  }

  stage('Test') {
    try {
      // sh 'scripts/install-flyway'
      withCredentials( [string(credentialsId: 'S3_ACCESS_KEY',
                               variable:      'S3_ACCESS_KEY'),
                        string(credentialsId: 'S3_SECRET_KEY',
                               variable:      'S3_SECRET_KEY'),
                        string(credentialsId: 'S3_ENDPOINT',
                               variable:      'S3_ENDPOINT'),
                        string(credentialsId: 'SYSREV_DEV_KEY',
                               variable:      'SYSREV_DEV_KEY')] ) {
        withEnv( ['DB_NAME=datasource_test',
                  'DB_HOST=localhost',
                  'DB_PORT=5432',
                  'DB_PASSWORD=""',
                  'DB_USER=postgres'] ) {
          sh 'lein test'
          currentBuild.result = 'SUCCESS'
        }
      }
    } catch (e) {
      currentBuild.result = 'FAILURE'
      // sendSlackMsgFull('Tests failed')
      throw e
    } finally {
      if (currentBuild.result == 'SUCCESS') {
        // sendSlackMsgFull ('Tests passed','blue')
      }
    }
  }
  stage('Deploy') {
    if (branch == 'production') {
      try {
        if (currentBuild.result == 'SUCCESS') {
          echo "Deploying ${branch}"
          // you will need a dir structure like
          // $DEPLOY_DIR
          // |
          // +- docker/datasource/
          // +- resources/public/
          // +- scripts/run-server-jar (based on scripts/run-server-jar.template in repo)
          // +- src/sql/schema/
          // +- target/
          withEnv( ['DATASOURCE_HOST=sysrev.com',
                    'DEPLOY_DIR=/home/ubuntu/datasource-deploy',
                    'DOCKER_DIR=docker/datasource'] ) {
            echo "Building database" // needs to be done
            echo "Pushing branch to $DATASOURCE_HOST"
            sshagent( credentials:['5e64049c-e099-4aa4-9fb0-3a126aac09db'] ) {
              withCredentials( [string(credentialsId: 'DATASOURCE_USER',
                                       variable:      'DATASOURCE_USER')] ) {
                echo 'Building standalone jar'
                sh 'lein with-profile +prod uberjar'
                echo "Transferring files to ${DATASOURCE_HOST}"
                remoteHost =       "${DATASOURCE_USER}@${DATASOURCE_HOST}"
                remoteDeployPath = "${remoteHost}:${DEPLOY_DIR}"
                remoteDockerPath = "${remoteDeployPath}/${DOCKER_DIR}"
                sh "scp target/datasource-server.jar ${remoteDeployPath}/target"
                sh "scp -r src/sql/schema ${remoteDeployPath}/src/sql"
                sh "scp -r resources ${remoteDeployPath}"
                sh "scp scripts/install-flyway ${remoteDeployPath}/scripts"
                sh "scp ${DOCKER_DIR}/Dockerfile ${remoteDockerPath}"
                sh "scp ${DOCKER_DIR}/docker-compose.yml ${remoteDockerPath}"
                echo 'Installing and running flyway on server'
                sh "ssh ${remoteHost} \
                    \"cd ${DEPLOY_DIR} ; \
                      ./scripts/install-flyway\""
                sh "ssh ${remoteHost} \
                    \"cd ${DEPLOY_DIR} ; \
                      ./flyway -user=postgres -password= \
                               -url=jdbc:postgresql://localhost:5431/datasource \
                               -locations=filesystem:./src/sql/schema \
                               migrate\""
                echo "Restarting docker container on ${DATASOURCE_HOST}"
                sh "ssh ${remoteHost} \
                    \"cd ${DEPLOY_DIR} ; \
                      docker-compose -f ${DOCKER_DIR}/docker-compose.yml up --build -d\""
              }
            }
          }
          // you will need a dir structure like
          // $DEPLOY_DIR
          // |
          // +- docker/scraper/
          // +- logs/
          // +- resources/pubmed/
          // +- scripts/run-scraper-service (based on scripts/run-scraper-service.template in repo)
          // +- ssh-keys/id_rsa.datasource
          // +- target/
          sshagent( credentials:['id_rsa.datasource'] ) {
            withEnv( ['WS1=insilica-ws-1.ddns.net',
                      'USER=james',
                      'DEPLOY_DIR=/home/james/scraper-deploy',
                      'DOCKER_DIR=docker/scraper'] ) {
              remoteHost =       "${USER}@${WS1}"
              remoteDeployPath = "${remoteHost}:${DEPLOY_DIR}"
              remoteDockerPath = "${remoteDeployPath}/${DOCKER_DIR}"
              echo 'Building scraper uberjar'
              sh 'lein with-profile +scraper uberjar'
              echo "Transferring files to ${WS1}"
              sh "scp target/scraper-service.jar ${remoteDeployPath}/target"
              sh "scp -r resources/pubmed ${remoteDeployPath}/resources"
              sh "scp ${DOCKER_DIR}/Dockerfile ${remoteDockerPath}"
              sh "scp ${DOCKER_DIR}/docker-compose.yml ${remoteDockerPath}"
              echo "Restarting docker container on ${WS1}"
              sh "ssh ${remoteHost} \
                  \"cd ${DEPLOY_DIR} ; \
                    docker-compose -f ${DOCKER_DIR}/docker-compose.yml up --build -d\""
            }
          }
          currentBuild.result = 'SUCCESS'
        }
      } catch (e) {
        currentBuild.result = 'FAILURE'
        // sendSlackMsg ('Deploy failed')
      } finally {
        if (currentBuild.result == 'SUCCESS') {
          // sendSlackMsgFull ('Deployed to AWS', 'blue')
        }
      }
    }
  }
}
