package spring

import dueuno.elements.providers.DueunoAuthenticationDetailsSource
import dueuno.elements.providers.DueunoUserDetailsService
import dueuno.elements.providers.ExternalIDAuthenticationProvider

// Place your Spring DSL code here
beans = {
    authenticationDetailsSource(DueunoAuthenticationDetailsSource)
    dueunoUserDetailsService(DueunoUserDetailsService) {
        grailsApplication = grailsApplication
    }
    externalIDAuthenticationProvider(ExternalIDAuthenticationProvider) {
        dueunoUserDetailsService = dueunoUserDetailsService
    }
}
