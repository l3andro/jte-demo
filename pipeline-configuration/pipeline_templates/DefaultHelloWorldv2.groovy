/* build()
static_code_analysis()
deploy_to dev 

if(requiresApproval){
    timeout(time: 5, unit: 'MINUTES') {
        input 'Approve the deployment?'
    }
} */

//deploy_to prod