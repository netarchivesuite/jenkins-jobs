def nas = 'NetarchiveSuite'
def template = nas+'-template'
def giturl = 'https://github.com/netarchivesuite/netarchivesuite.git'
def branchBuilds = ['h3']

branchBuilds.each {
    def branch = it
    job(type: Maven) {
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
        git(giturl)
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
    description "Check out the latest version from github at midnight and:\n" +
            "<ul>\n" +
            "<li>Creates a release package and scp's it to the test system (with timestamp = svn revision)\n" +
            "<li>Starts the test system.\n" +
            "<li>Runs the system tests. \n" +
            "</ul>\n" +
            "  <b>Target: mvn clean install -PsystemTest</b>  "

    goals "clean integration-test -PsystemTest"

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