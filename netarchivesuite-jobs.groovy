def project = 'NetarchiveSuite'
def giturl = 'https://github.com/netarchivesuite/netarchivesuite.git'
def branchBuilds = ['master', 'NAS-2383']

def javaVersion = 'java8'
def mavenVersion = 'maven3.2'

branchBuilds.each {
    def branch = it
    job(type: Maven) {
        name "NetarchiveSuite-${branch}"
        description "Branch ${branch} build"
        scm {
            git(giturl, branch)
        }
        jdk javaVersion
        mavenInstallation mavenVersion
        goals "clean install -PfullTest"
        authorization {
            permission('hudson.model.Run.Update:netarchivesuite-devel')
            permission('hudson.model.Item.Build:netarchivesuite-devel')
            permission('hudson.model.Item.Read:anonymous')
            permission('hudson.model.Item.Read:netarchivesuite-devel')
            permission('hudson.model.Item.Workspace:netarchivesuite-devel')
            permission('hudson.model.Item.Release:netarchivesuite-devel')
            permission('hudson.model.Item.Discover:netarchivesuite-devel')
            permission('hudson.model.Run.Delete:netarchivesuite-devel')
            permission('hudson.model.Item.Cancel:netarchivesuite-devel')
            permission('hudson.model.Item.Configure:netarchivesuite-devel')
        }
        triggers {
            githubPush()
        }

        publishers {
            mailer('', true, true)
        }
    }
}
