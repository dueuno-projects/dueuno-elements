package dueuno
import grails.plugin.geb.ContainerGebSpec
import grails.testing.mixin.integration.Integration

/**
 * See https://docs.grails.org/latest/guide/testing.html#functionalTesting and https://www.gebish.org/manual/current/
 * for more instructions on how to write functional tests with Grails and Geb.
 */
@Integration
class Grails7M1Spec extends ContainerGebSpec {

    void 'should display the correct title on the home page'() {
        when: 'visiting the home page'
            go '/'

        then: 'the page title is correct'
            title == 'Welcome to Grails'
    }
}
