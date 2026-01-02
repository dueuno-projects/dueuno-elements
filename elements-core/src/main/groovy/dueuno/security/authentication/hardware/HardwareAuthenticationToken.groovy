package dueuno.security.authentication.hardware

import groovy.transform.CompileStatic
import org.springframework.security.authentication.AbstractAuthenticationToken

@CompileStatic
class HardwareAuthenticationToken extends AbstractAuthenticationToken {

    final String token
    final Object principal

    HardwareAuthenticationToken(String token) {
        super(null)
        this.token = token
        this.principal = null
        setAuthenticated(false)
    }

    HardwareAuthenticationToken(Object principal, Collection authorities) {
        super(authorities)
        this.principal = principal
        this.token = null
        setAuthenticated(true)
    }

    @Override
    Object getCredentials() {
        null
    }

    @Override
    Object getPrincipal() {
        principal
    }
}