/**
* Receive the name of a branch and return the specific test args for this branch
*
* @param branchName the name of the branch to get configuration
* @return (Map) returns a map containing the branch configurations comming from pipeline's pipeline_config.groovy
*/
void call(String branchName) {
  def environments = branch_parameters.get(branch_parameters.desenvolvimento, branch_parameters.desenvolvimento)

    environments.each {
        key, value -> def env.key = value
    }
}

        
        

        //environments.each {
       //     key, value -> def env.key = value
       // }

        //return branchEnvironments.get(branch, branchEnvironments.get('desenvolvimento'))
