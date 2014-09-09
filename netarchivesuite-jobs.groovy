def project = 'NetarchiveSuite'

def giturl = 'https://github.com/netarchivesuite/netarchivesuite/'

def branchBuilds = ['master', 'NAS-2383']

branchBuilds.each {
    def branch = it
    job(type: Maven) {
        name "NetarchiveSuite-${branch}"
        description "Branch ${branch} build"
        scm {
            git("https://github.com/netarchivesuite/netarchivesuite.git", branch)
        }
        jdk 'java8'
        mavenInstallation 'maven3.2'
        goals "clean install -PfullTest"
        triggers {
            githubPush()
        }

        publishers {
            mailer('', true, true)
        }
    }
}
