
@merge jte{
  permissive_initialization = true
}

@merge libraries{
    maven
    splunk{
        afterSteps = [ "static_code_analysis", "unit_test"  ]
    }
}

application_environments{
    dev{
        ip_addresses = [ "0.0.0.1", "0.0.0.2" ]
    }
    prod{
        long_name = "Production"
        ip_addresses = [ "0.0.1.1", "0.0.1.2", "0.0.1.3", "0.0.1.4" ]
    }
}