package dueuno.elements.providers

import groovy.transform.CompileStatic
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

@CompileStatic
class ExternalIDAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private DueunoUserDetailsService dueunoUserDetailsService

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

        Object details = authentication.details

        if ( !(details instanceof DueunoAuthenticationDetails) ) {
            logger.debug("Authentication failed: authenticationToken principal is not a ExternalIDPrincipal")
            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"))
        }

        def externalIDAuthenticationDetails = details as DueunoAuthenticationDetails

        if (externalIDAuthenticationDetails.externalID != userDetails.username) {
            logger.debug("Failed to authenticate since external id does not match stored value")
            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"))
        }
    }

    @Override
    protected final UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        try {
            UserDetails loadedUser = this.getDueunoUserDetailsService().loadUserByExternalID(username)
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
}
