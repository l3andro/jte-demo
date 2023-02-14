def configBuildConfig(def destPrjName, def branchName){
  openshift.withProject("${destPrjName}") {
    echo "Update buildConfig ${destPrjName}"
    final buildFiles = sh(script: "ls -1v ${workspace}/etc/openshift/ci-tools/build*", returnStdout: true).split()
    if(!buildFiles){
      error("No build(s) config was found in the folder ${workspace}/etc/openshift/ci-tools/build* for this app!")
    }
    echo "Build(s) found: ${buildFiles}"
    buildFiles.each { buildFile ->
      def buildAsFile = new File(buildFile)
      String buildWithoutExt = buildAsFile.name.take(buildAsFile.name.lastIndexOf('.'))
      echo "Update ${buildAsFile.name} in the project ${destPrjName} with environment variables of ${buildWithoutExt}.env"
      def buildModel = openshift.process(readFile("${buildFile}"), "--param-file", "${workspace}/etc/openshift/${branchName}/${buildWithoutExt}.env")
      openshift.apply(buildModel)
      echo "Apply BuildConfig ${buildAsFile.name} in the project ${destPrjName}"
    }
  }
}

def configSetupCredentials(def destPrjName, def branchName){
  openshift.withProject("${destPrjName}") {
    echo "Update Credentials ${destPrjName}"
    final setupFiles = sh(script: "ls -1v ${workspace}/etc/openshift/ci-tools/setup*", returnStdout: true).split()
    if(!setupFiles){
      error("No setup(s) config was found in the folder ${workspace}/etc/openshift/ci-tools/setup* for this app!")
    }
    echo "Build(s) found: ${setupFiles}"
    setupFiles.each { setupFile ->
      def setupAsFile = new File(setupFile)
      String setupWithoutExt = setupAsFile.name.take(setupAsFile.name.lastIndexOf('.'))
      echo "Update ${setupAsFile.name} in the project ${destPrjName} with environment variables of ${setupWithoutExt}.env"
      def setupModel = openshift.process(readFile("${setupFile}"), "--param-file", "${workspace}/etc/openshift/${branchName}/${setupWithoutExt}.env")
      openshift.apply(setupModel)
      echo "Apply Setup ${setupAsFile.name} in the project ${destPrjName}"
    }
  }
}


def call(def buildClusterName, def buildProjectName, def projectName, def clusterName, def branchName){
  script {
    echo "Configuration of projects and builds in the cluster of the development and approval environment"
    buildClusterName=(buildClusterName=="default"?"":buildClusterName)
    echo "BUILD CLUSTER NAME ${buildClusterName}"
    echo "CLUSTER NAME ${clusterName}"
    echo "Fixing Log4J vulnerability CVE-2021-44228"
    sh '''
      if grep -q Dlog4j2.formatMsgNoLookups=true Dockerfile; then
        echo "O projeto já está ajustado em relação ao Log4J"
      else
        sed -ie 's|"java",|"java", "-Dlog4j2.formatMsgNoLookups=true",|' Dockerfile
      fi
    '''
    if (branchName == "master") {
      echo "Configuration of the ${projectName} on the production cluster ${clusterName}"
      openshift.withCluster(clusterName) {
        //openshift.verbose()
        echo "${openshift.raw("whoami")}"
        echo "Hello from ${openshift.cluster()}'s default project: ${openshift.project()}"
        try {
          newProject = openshift.newProject( "${projectName}" )
          println "New project Create: ${newProject}"
        }   catch ( e ){
          println "The project exist: ${e}"
        }
      }
      openshift.withCluster(buildClusterName) {
        //openshift.logLevel(1)
        sh ("sed -ie 's|registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift|registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift:1.8|' Dockerfile")
        echo "User Account - cluster dev and hom"
        echo "${openshift.raw("whoami")}"
        echo "Hello from ${openshift.cluster()}'s default project: ${openshift.project()}"
        try {
          newProject = openshift.newProject( "${buildProjectName}")
          println "New project Create: ${newProject}"
        } catch ( e ){
          println "The project exist: ${e}"
        }
        configSetupCredentials(buildProjectName, branchName)
        configBuildConfig(buildProjectName, branchName)
      }
    }else {
      echo "Configuration of the ${projectName} on the DEV/UAT cluster ${clusterName}"
      sh ("sed -ie 's|registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift|registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift:1.8|' Dockerfile")
      if ( branchName == "desenvolvimento") {
          sh ("sed -ie 's|MAX_REPLICATION =.*|MAX_REPLICATION = 1|' etc/openshift/desenvolvimento/deployment.env")
        } else {
          sh ("sed -ie 's|MAX_REPLICATION =.*|MAX_REPLICATION = 1|' etc/openshift/homologacao/deployment.env")
      }
      openshift.withCluster(buildClusterName) {
        //openshift.logLevel(1)
        echo "User Account - cluster dev and hom"
        echo "${openshift.raw("whoami")}"
        echo "Hello from ${openshift.cluster()}'s default project: ${openshift.project()}"
        try {
          newProject = openshift.newProject( "${buildProjectName}")
          println "New project Create: ${newProject}"
        } catch ( e ){
          println "The project exist: ${e}"
        }
        configSetupCredentials(buildProjectName, branchName)
        configBuildConfig(buildProjectName, branchName)
      }
    }
  }
}