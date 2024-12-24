package dueuno.elements.security

import grails.plugin.springsecurity.userdetails.GrailsUser
import groovy.transform.CompileStatic
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

@CompileStatic
class DueunoElementsUserDetails extends GrailsUser {

    String tenantId

    // System fields
    boolean deletable
    String defaultGroup
    String note

    // User data
    String firstname
    String lastname
    String language
    String email
    String telephone

    // Session data
    Integer sessionDuration
    Integer rememberMeDuration

    // User preferences
    String decimalFormat
    boolean prefixedUnit
    boolean symbolicCurrency
    boolean symbolicQuantity
    boolean invertedMonth
    boolean twelveHours
    boolean firstDaySunday
    Integer fontSize
    boolean animations

    DueunoElementsUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
                              boolean credentialsNonExpired, boolean accountNonLocked,
                              Collection<? extends GrantedAuthority> authorities, long id) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities, id)
        tenantId = 'DEFAULT'
    }
}
