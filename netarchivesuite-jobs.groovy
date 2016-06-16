def nas = 'NetarchiveSuite'
def template = nas+'-template'
def giturl = 'https://github.com/netarchivesuite/netarchivesuite.git'
def branchBuilds = ['h3']

branchBuilds.each {
    def branch = it
    delegate.job(type: Maven) {
        using template
        name "${nas}-${branch}"
        description "Branch ${branch} build"
        scm {
            git(giturl, branch)
        }
        triggers {
            githubPush()
        }

        goals "clean test -PfullTest"
    }
}

job(type: Maven) {
    using template
    name "${nas}"
    description "Build, test and deploy master"
    scm {
        git(giturl, 'master')
    }
    triggers {
        githubPush()
    }

    goals "clean deploy -PfullTest"
}

job(type: Maven) {
    using template
    name "${nas}-sonar"
    description "Full build with publishing of code analysis to SBForge Sonar"

    goals "clean install -PfullTest"

    triggers {
        cron('0 0 * * *')
    }

    configure { project ->
        project / publishers << 'hudson.plugins.sonar.SonarPublisher' {
            jdk('(Inherit From Job)')


            branch()
            language()
            mavenOpts()
            jobAdditionalProperties()
            settings(class: 'jenkins.mvn.DefaultSettingsProvider')
            globalSettings(class: 'jenkins.mvn.DefaultGlobalSettingsProvider')
            usePrivateRepository(false)
        }
    }
}

job(type: Maven) {
    using template
    name "${nas}-system-test"
    description
            "<ul>\n" +
            "<li>Deploys the newest NetarchiveSuite zip SNAPSHOT in the m2 repository to the test system under the devel user.\n" +
            "<li>Starts the test system.\n" +
            "<li>Runs the system tests. \n" +
            "</ul>\n" +
            "  <b>Target: mvn clean install -PsystemTest -rf :system-test</b>  "

    goals "clean integration-test -PsystemTest -rf :system-test"

    triggers {
        cron('0 0 * * *')
    }

    publishers {
        downstream("${nas}-publish-artifacts", 'SUCCESS')
    }
}

job(type: Maven) {
    using template
    name "${nas}-publish-artifacts"
    description "Publishes NAS artifacts if the system test passes."

    goals "clean deploy -DskipTests"
    publishers {
        archiveArtifacts 'deploy/distribution/target/NetarchiveSuite*.zip'
    }
}



job(type: Maven) {
    using template
    name "${nas}-webdanica"
    description "Build, test and deploy master"
    scm {
        git('https://github.com/netarchivesuite/webdanica.git', 'master')
    }
    triggers {
        githubPush()
    }

    goals "clean deploy -PfullTest"
}
