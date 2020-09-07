pipeline {
  agent {
    dockerfile true
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
            def phase = env.BRANCH_NAME == 'master' ? 'deploy' : 'verify'
            maven cmd: "clean ${phase} " +
                '-Dproject-build-plugin.version=9.1.0 ' +
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
