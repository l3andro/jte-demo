/**
* Receive the name of a branch and return the specific test args for this branch
*
* @param branchName the name of the branch to get configuration
* @return (Map) returns a map containing the branch configurations comming from pipeline's pipeline_config.groovy
*/
import hudson.EnvVars;
import hudson.slaves.EnvironmentVariablesNodeProperty;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import hudson.util.DescribableList;
import jenkins.model.Jenkins;
public createGlobalEnvironmentVariables(String key, String value){

       Jenkins instance = Jenkins.getInstance();

       DescribableList<NodeProperty<?>, NodePropertyDescriptor> globalNodeProperties = instance.getGlobalNodeProperties();
       List<EnvironmentVariablesNodeProperty> envVarsNodePropertyList = globalNodeProperties.getAll(EnvironmentVariablesNodeProperty.class);

       EnvironmentVariablesNodeProperty newEnvVarsNodeProperty = null;
       EnvVars envVars = null;

       if ( envVarsNodePropertyList == null || envVarsNodePropertyList.size() == 0 ) {
           newEnvVarsNodeProperty = new hudson.slaves.EnvironmentVariablesNodeProperty();
           globalNodeProperties.add(newEnvVarsNodeProperty);
           envVars = newEnvVarsNodeProperty.getEnvVars();
       } else {
           envVars = envVarsNodePropertyList.get(0).getEnvVars();
       }
       envVars.put(key, value)
       instance.save()
}

void call(String branchName) {
  def environments = branch_parameters.get(branch_parameters.desenvolvimento, branch_parameters.desenvolvimento)

    environments.each {
        key, value -> createGlobalEnvironmentVariables(key,value)
    }
}


