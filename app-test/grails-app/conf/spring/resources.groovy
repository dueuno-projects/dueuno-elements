package spring

import dueuno.elements.security.CustomUserDetailsService
import dueuno.elements.security.ExternalIdAuthenticationFilter
import dueuno.elements.security.ExternalIdAuthenticationProvider

// Place your Spring DSL code here
beans = {
    customUserDetailsService(CustomUserDetailsService) {
        grailsApplication = ref('grailsApplication')
    }

    externalIdAuthenticationProvider(ExternalIdAuthenticationProvider) {
        customUserDetailsService = ref('customUserDetailsService')
    }

    externalIdAuthenticationFilter(ExternalIdAuthenticationFilter, '/api/auth/external') {
        authenticationManager = ref('authenticationManager')
        authenticationSuccessHandler = ref('authenticationSuccessHandler')
        authenticationFailureHandler = ref('authenticationFailureHandler')
        sessionAuthenticationStrategy = ref('sessionAuthenticationStrategy')
        rememberMeServices = ref('rememberMeServices')
        securityContextRepository = ref('securityContextRepository')
    }
}
