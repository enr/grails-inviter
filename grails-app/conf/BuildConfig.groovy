grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    
    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }
    
    dependencies {
		build "org.scribe:scribe:1.3.2"
		runtime "org.scribe:scribe:1.3.2"
        test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
    }
    
    plugins {
        build(":release:2.0.4",
              ":rest-client-builder:1.0.2") {
            export = false
        }
        compile ":mail:1.0"
        compile ":rest:0.7"
        runtime ":resources:1.2.RC2"

        test(":spock:0.7") {
            exclude "spock-grails-support"
        }
    }
}
