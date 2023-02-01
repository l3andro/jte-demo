@BeforeStep
void before(){
  println "Splunk: running before the ${context.library} library's ${context.step} step" 
}

@AfterStep({ hookContext.step in config.afterSteps })
void after(){
  println "Splunk: running after the ${context.library} library's ${context.step} step" 
}

@AfterStep({ currentBuild.result.toString() == "FAILURE" })
void afterFailure(context){
  println "Splunk: running after the ${context.library} library's ${context.step} step failure"  
}
