package parameters

class BranchParameters implements Serializable {
    static Map get(Map branchParameters, String branch) {
        return branchParameters.get(branch, branchParameters.get('desenvolvimento'))
    }
}