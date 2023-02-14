
@merge libraries{
    sonarqube
    openshift
    splunk{
        afterSteps = [ "static_code_analysis", "unit_test"  ]
    }
}