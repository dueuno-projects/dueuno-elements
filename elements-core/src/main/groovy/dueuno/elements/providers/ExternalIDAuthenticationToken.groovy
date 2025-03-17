package dueuno.elements.providers

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class ExternalIDAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal

    public ExternalIDAuthenticationToken(Object principal) {
        super(null)
        this.principal = principal
        setAuthenticated(false)
    }

    public ExternalIDAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities)
        this.principal = principal
        super.setAuthenticated(true) // must use super, as we override
    }

    public String getExternalID() {
        return (this.getPrincipal() == null) ? "" : this.getPrincipal().toString()
    }

    public static ExternalIDAuthenticationToken unauthenticated(Object principal) {
        return new ExternalIDAuthenticationToken(principal)
    }

    public static ExternalIDAuthenticationToken authenticated(Object principal, Collection<? extends GrantedAuthority> authorities) {
        return new ExternalIDAuthenticationToken(principal, authorities)
    }

    @Override
    public Object getCredentials() {
        return null
    }

    @Override
    public Object getPrincipal() {
        return this.principal
    }
}
