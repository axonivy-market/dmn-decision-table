pipeline {
  agent {
    dockerfile true
  }

  parameters {
    choice(
        name: 'DEPLOY_PROFILE',
        choices: ['central']
    )
  }

  options {
    buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '5'))
  }

  triggers {
    cron '@midnight'
  }

  stages {
    stage('build') {
      steps {
          script {
            maven cmd: 'clean deploy ' + 
                '-P ${DEPLOY_PROFILE} ' + 
                '-Dproject-build-plugin.version=8.0.5-SNAPSHOT ' +
                '-Dgpg.skip=true ' +
                '-Divy.engine.list.url=https://jenkins.ivyteam.io/job/ivy-core_product/job/master/lastSuccessfulBuild/ ' + 
                '-Divy.engine.directory=${WORKSPACE}/ldap-beans/target/ivyEngine '
            
            archiveArtifacts '*/target/*.jar, */*/target/*.jar,  */samples/*/target/*.iar'
            junit '**/target/surefire-reports/**/*.xml' 
          }
      }
    }
  }
}
