def nas = 'NetarchiveSuite'
def giturl = 'https://github.com/netarchivesuite/netarchivesuite.git'
def branchBuilds = ['NAS-2383', 'NAS-2124']

branchBuilds.each {
    def branch = it
    job(type: Maven) {
        using nas
        name "${nas}-${branch}"
        description "Branch ${branch} build"
        scm {
            git(giturl, branch)
        }
        goals "clean install -PfullTest"
    }
}