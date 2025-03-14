package dueuno.elements.providers

import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.userdetails.GormUserDetailsService
import grails.plugin.springsecurity.userdetails.NoStackUsernameNotFoundException
import groovy.util.logging.Slf4j
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

@Slf4j
class DueunoUserDetailsService extends GormUserDetailsService {

    @Transactional(readOnly=true, noRollbackFor=[IllegalArgumentException, UsernameNotFoundException])
    UserDetails loadUserByExternalID(String externalID, boolean loadRoles) throws UsernameNotFoundException {

        def conf = SpringSecurityUtils.securityConfig
        String userClassName = conf.userLookup.userDomainClassName
        def dc = grailsApplication.getArtefact 'Domain', userClassName
        if (!dc) {
            throw new IllegalArgumentException("The specified user domain class '$userClassName' is not a domain class")
        }

        Class<?> User = dc.clazz

        def user = User.createCriteria().get {
            eq((conf.userLookup.externalIDPropertyName), externalID)
        }

        if (!user) {
            log.warn 'User not found: {}', externalID
            throw new NoStackUsernameNotFoundException()
        }

        Collection<GrantedAuthority> authorities = loadAuthorities(user, externalID, loadRoles)
        createUserDetails user, authorities
    }

    UserDetails loadUserByExternalID(String externalID) throws UsernameNotFoundException {
        loadUserByExternalID externalID, true
    }
}
