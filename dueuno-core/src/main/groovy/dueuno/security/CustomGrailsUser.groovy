package dueuno.security

import grails.plugin.springsecurity.userdetails.GrailsUser
import groovy.transform.CompileStatic
import org.springframework.security.core.GrantedAuthority

@CompileStatic
class CustomGrailsUser extends GrailsUser {

    final String externalId

    CustomGrailsUser(String username, String password, boolean enabled, boolean accountNonExpired,
                     boolean credentialsNonExpired, boolean accountNonLocked,
                     Collection<? extends GrantedAuthority> authorities, Long id, String externalId) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities, id)
        this.externalId = externalId
    }

    String getExternalId() {
        return this.externalId
    }
}
