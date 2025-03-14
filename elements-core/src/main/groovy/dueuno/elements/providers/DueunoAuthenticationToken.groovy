package dueuno.elements.providers

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.AuthenticatedPrincipal
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

import java.security.Principal

class DueunoAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal

    private Object credentials

    public DueunoAuthenticationToken(Object principal, Object credentials) {
        super(null)
        this.principal = principal
        this.credentials = credentials
        setAuthenticated(false)
    }

    public DueunoAuthenticationToken(Object principal, Object credentials,
                                               Collection<? extends GrantedAuthority> authorities) {
        super(authorities)
        this.principal = principal
        this.credentials = credentials
        super.setAuthenticated(true) // must use super, as we override
    }

    public String getExternalID() {
        if (this.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) this.getPrincipal()).getUsername();
        }
        if (this.getPrincipal() instanceof AuthenticatedPrincipal) {
            return ((AuthenticatedPrincipal) this.getPrincipal()).getName();
        }
        if (this.getPrincipal() instanceof Principal) {
            return ((Principal) this.getPrincipal()).getName();
        }
        return (this.getPrincipal() == null) ? "" : this.getPrincipal().toString();
    }

    @Override
    public Object getCredentials() {
        return this.credentials
    }

    @Override
    public Object getPrincipal() {
        return this.principal
    }
}
