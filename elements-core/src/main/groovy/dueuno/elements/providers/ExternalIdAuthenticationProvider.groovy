package dueuno.elements.providers

import groovy.transform.CompileStatic
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceAware
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.authentication.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.SpringSecurityMessageSource
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper
import org.springframework.security.core.userdetails.UserCache
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsChecker
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.userdetails.cache.NullUserCache
import org.springframework.util.Assert

@CompileStatic
class ExternalIdAuthenticationProvider implements AuthenticationProvider, InitializingBean, MessageSourceAware {

    protected final Log logger = LogFactory.getLog(getClass())

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor()

    private UserCache userCache = new NullUserCache()

    private boolean forcePrincipalAsString = false

    protected boolean hideUserNotFoundExceptions = true

    private UserDetailsChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks()

    private UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks()

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper()

    private DueunoUserDetailsService dueunoUserDetailsService

    @Override
    public final void afterPropertiesSet() throws Exception {
        Assert.notNull(this.userCache, "A user cache must be set")
        Assert.notNull(this.messages, "A message source must be set")
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource)
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(ExternalIDAuthenticationToken.class, authentication,
                () -> this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.onlySupports",
                        "Only ExternalIDAuthenticationToken is supported"))
        String externalID = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" :
                ((ExternalIDAuthenticationToken) authentication).getExternalID()
        boolean cacheWasUsed = true
        UserDetails user = this.userCache.getUserFromCache(externalID)
        if (user == null) {
            cacheWasUsed = false
            try {
                user = retrieveUser(externalID)
            }
            catch (UsernameNotFoundException ex) {
                this.logger.debug("Failed to find user '" + externalID + "'")
                if (!this.hideUserNotFoundExceptions) {
                    throw ex
                }
                throw new BadCredentialsException(this.messages
                        .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"))
            }
            Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract")
        }
        try {
            this.preAuthenticationChecks.check(user)
            additionalAuthenticationChecks((ExternalIDAuthenticationToken) authentication)
        }
        catch (AuthenticationException ex) {
            if (!cacheWasUsed) {
                throw ex
            }
            // There was a problem, so try again after checking
            // we're using latest data (i.e. not from the cache)
            cacheWasUsed = false
            user = retrieveUser(externalID)
            this.preAuthenticationChecks.check(user)
            additionalAuthenticationChecks((ExternalIDAuthenticationToken) authentication)
        }
        this.postAuthenticationChecks.check(user)
        if (!cacheWasUsed) {
            this.userCache.putUserInCache(user)
        }
        Object principalToReturn = user
        if (this.forcePrincipalAsString) {
            principalToReturn = user.getUsername()
        }
        return createSuccessAuthentication(principalToReturn, authentication, user)
    }

    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        ExternalIDAuthenticationToken result = ExternalIDAuthenticationToken.authenticated(principal,
                this.authoritiesMapper.mapAuthorities(user.getAuthorities()))
        result.setDetails(authentication.getDetails())
        this.logger.debug("Authenticated user")
        return result
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (ExternalIDAuthenticationToken.class.isAssignableFrom(authentication))
    }

    protected void additionalAuthenticationChecks(ExternalIDAuthenticationToken authentication) throws AuthenticationException {

        Object details = authentication.details

        if ( !(details instanceof DueunoAuthenticationDetails) ) {
            logger.debug("Authentication failed: authenticationToken principal is not a ExternalIDPrincipal")
            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"))
        }

        /*def externalIDAuthenticationDetails = details as DueunoAuthenticationDetails

        if (externalIDAuthenticationDetails.externalID != userDetails.username) {
            logger.debug("Failed to authenticate since external id does not match stored value")
            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"))
        }*/
    }

    protected final UserDetails retrieveUser(String externalID)
            throws AuthenticationException {
        try {
            UserDetails loadedUser = this.getDueunoUserDetailsService().loadUserByExternalID(externalID)
            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException(
                        "UserDetailsService returned null, which is an interface contract violation")
            }
            return loadedUser
        }
        catch (UsernameNotFoundException ex) {
            throw ex
        }
        catch (InternalAuthenticationServiceException ex) {
            throw ex
        }
        catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex)
        }
    }

    public void setDueunoUserDetailsService(DueunoUserDetailsService dueunoUserDetailsService) {
        this.dueunoUserDetailsService = dueunoUserDetailsService
    }

    protected DueunoUserDetailsService getDueunoUserDetailsService() {
        return this.dueunoUserDetailsService
    }

    private class DefaultPreAuthenticationChecks implements UserDetailsChecker {

        @Override
        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                ExternalIdAuthenticationProvider.this.logger
                        .debug("Failed to authenticate since user account is locked")
                throw new LockedException(ExternalIdAuthenticationProvider.this.messages
                        .getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"))
            }
            if (!user.isEnabled()) {
                ExternalIdAuthenticationProvider.this.logger
                        .debug("Failed to authenticate since user account is disabled")
                throw new DisabledException(ExternalIdAuthenticationProvider.this.messages
                        .getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"))
            }
            if (!user.isAccountNonExpired()) {
                ExternalIdAuthenticationProvider.this.logger
                        .debug("Failed to authenticate since user account has expired")
                throw new AccountExpiredException(ExternalIdAuthenticationProvider.this.messages
                        .getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"))
            }
        }

    }

    private class DefaultPostAuthenticationChecks implements UserDetailsChecker {

        @Override
        public void check(UserDetails user) {
            if (!user.isCredentialsNonExpired()) {
                ExternalIdAuthenticationProvider.this.logger
                        .debug("Failed to authenticate since user account credentials have expired")
                throw new CredentialsExpiredException(ExternalIdAuthenticationProvider.this.messages
                        .getMessage("AbstractUserDetailsAuthenticationProvider.credentialsExpired",
                                "User credentials have expired"))
            }
        }

    }
}
