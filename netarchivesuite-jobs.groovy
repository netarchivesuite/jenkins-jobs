def project = 'NetarchiveSuite'

def giturl = 'https://github.com/netarchivesuite/netarchivesuite/'

def branchBuilds = ['master']


def it = 'master'
//branchBuilds.each {
job {
    name '${project}-${it}'
    description 'Default build on the branch ${it} branch'
    scm {
        git {
            remote {
                url($ { giturl }, $ { it })
            }
        }
    }
    steps {
        maven("clean install -PfullTest")
    }
//    triggers {
//        githubPush()
//    }
//    jdk('java8')

//    mavenInstallation('maven3.2')
//    publishers {
//        mailer('', false, true)
//    }
//    }
}
