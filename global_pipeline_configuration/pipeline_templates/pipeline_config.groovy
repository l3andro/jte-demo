
@merge libraries{
    sonarqube
    splunk{
        afterSteps = [ "static_code_analysis", "unit_test"  ]
    }
}