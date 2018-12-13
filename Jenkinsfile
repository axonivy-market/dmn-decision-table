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
            maven cmd: 'clean deploy ' + 
                '-P build ' + 
                '-Dproject-build-plugin.version=7.2.0 ' +
                '-Dgpg.skip=true ' +
                '-Divy.engine.list.url=http://zugprobldmas/job/Trunk_All/ ' + 
                '-Divy.engine.directory=${WORKSPACE}/ldap-beans/target/ivyEngine '
            
            archiveArtifacts '*/target/*.jar, */*/target/*.jar,  */samples/*/target/*.iar'
          }
      }
    }
  }
}
