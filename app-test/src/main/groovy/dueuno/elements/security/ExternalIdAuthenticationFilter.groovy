package dueuno.elements.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.RememberMeServices
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.security.web.context.SecurityContextRepository

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ExternalIdAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    ExternalIdAuthenticationFilter(String defaultFilterProcessesUrl,
                                   AuthenticationManager authenticationManager,
                                   AuthenticationSuccessHandler successHandler,
                                   AuthenticationFailureHandler failureHandler,
                                   SessionAuthenticationStrategy sessionAuthenticationStrategy,
                                   RememberMeServices rememberMeServices,
                                   SecurityContextRepository securityContextRepository) {
        super(defaultFilterProcessesUrl)
        this.authenticationManager = authenticationManager
        this.authenticationSuccessHandler = successHandler
        this.authenticationFailureHandler = failureHandler
        this.sessionAuthenticationStrategy = sessionAuthenticationStrategy
        this.rememberMeServices = rememberMeServices
        this.securityContextRepository = securityContextRepository
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod())
        }
        String externalId = request.getParameter('externalId')
        externalId = (externalId != null) ? externalId.trim() : ""

        ExternalIdAuthenticationToken authRequest = ExternalIdAuthenticationToken.unauthenticated(externalId)
        setDetails(request, authRequest)
        return this.authenticationManager.authenticate(authRequest)
    }

    protected void setDetails(HttpServletRequest request, ExternalIdAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request))
    }
}

