def nas = 'NetarchiveSuite'
def giturl = 'https://github.com/netarchivesuite/netarchivesuite.git'
def branchBuilds = ['NAS-2383', 'NAS-2124', 'H1']

branchBuilds.each {
    def branch = it
    job(type: Maven) {
        using nas
        name "${nas}-${branch}"
        description "Branch ${branch} build"
        scm {
            git(giturl, branch)
        }

        goals "clean test -PfullTest"

        configure { job ->
            def publishers = job / publishers
            publishers.setValue('')
        }
    }
}

job(type: Maven) {
    using nas
    name "${nas}-sonar"
    description "Full build with publishing of code analysis to SBForge Sonar"

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
    using nas
    name "${nas}-system-test"
    description "Check out the latest version from github at midnight and:\n" +
            "<ul>\n" +
            "<li>Creates a release package and scp's it to the test system (with timestamp = svn revision)\n" +
            "<li>Starts the test system.\n" +
            "<li>Runs the system tests. \n" +
            "</ul>\n" +
            "  <b>Target: mvn clean install -PsystemTest -rf integration-test</b>  "

    goals "clean install -PsystemTest -rf integration-test"

    triggers {
        cron('0 0 * * *')
    }
}