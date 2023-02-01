@BeforeStep
void before(){
  println "Splunk: running before the ${hookContext.library} library's ${hookContext.step} step" 
}

@AfterStep({ hookContext.step in config.afterSteps })
void after(){
  println "Splunk: running after the ${hookContext.library} library's ${hookContext.step} step" 
}

@AfterStep({ currentBuild.result.toString() == "FAILURE" })
void afterFailure(context){
  println "Splunk: running after the ${hookContext.library} library's ${hookContext.step} step failure"  
}
