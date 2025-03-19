package dueuno.elements.security

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.userdetails.GormUserDetailsService
import grails.plugin.springsecurity.userdetails.NoStackUsernameNotFoundException
import groovy.util.logging.Slf4j
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

@Slf4j
class CustomUserDetailsService extends GormUserDetailsService {

    @Transactional(readOnly=true, noRollbackFor=[IllegalArgumentException, UsernameNotFoundException])
    UserDetails loadUserByExternalId(String externalId, boolean loadRoles) throws UsernameNotFoundException {

        def conf = SpringSecurityUtils.securityConfig
        String userClassName = conf.userLookup.userDomainClassName
        def dc = grailsApplication.getArtefact 'Domain', userClassName
        if (!dc) {
            throw new IllegalArgumentException("The specified user domain class '$userClassName' is not a domain class")
        }

        Class<?> User = dc.clazz

        def user = User.createCriteria().get {
            eq((conf.externalId.propertyName), externalId)
        }

        if (!user) {
            log.warn 'User not found: {}', externalId
            throw new NoStackUsernameNotFoundException()
        }

        Collection<GrantedAuthority> authorities = loadAuthorities(user, externalId, loadRoles)
        createUserDetails user, authorities
    }

    UserDetails loadUserByExternalId(String externalId) throws UsernameNotFoundException {
        loadUserByExternalId externalId, true
    }

    @Override
    protected UserDetails createUserDetails(user, Collection<GrantedAuthority> authorities) {

        def conf = SpringSecurityUtils.securityConfig

        String usernamePropertyName = conf.userLookup.usernamePropertyName
        String passwordPropertyName = conf.userLookup.passwordPropertyName
        String enabledPropertyName = conf.userLookup.enabledPropertyName
        String accountExpiredPropertyName = conf.userLookup.accountExpiredPropertyName
        String accountLockedPropertyName = conf.userLookup.accountLockedPropertyName
        String passwordExpiredPropertyName = conf.userLookup.passwordExpiredPropertyName

        String username = user."$usernamePropertyName"
        String password = user."$passwordPropertyName"
        boolean enabled = enabledPropertyName ? user."$enabledPropertyName" : true
        boolean accountExpired = accountExpiredPropertyName ? user."$accountExpiredPropertyName" : false
        boolean accountLocked = accountLockedPropertyName ? user."$accountLockedPropertyName" : false
        boolean passwordExpired = passwordExpiredPropertyName ? user."$passwordExpiredPropertyName" : false

        new CustomGrailsUser(username, password, enabled, !accountExpired, !passwordExpired,
                !accountLocked, authorities, user.id, username)
    }
}
