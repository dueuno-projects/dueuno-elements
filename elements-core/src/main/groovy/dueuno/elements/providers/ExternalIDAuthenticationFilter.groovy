package dueuno.elements.providers

import org.springframework.lang.Nullable
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ExternalIDAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login",
            "POST")

    private boolean postOnly = true;

    public ExternalIDAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER)
    }

    public ExternalIDAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager)
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod())
        }
        String externalID = obtainExternalID(request)
        externalID = (externalID != null) ? externalID.trim() : ""
        ExternalIDAuthenticationToken authRequest = ExternalIDAuthenticationToken.unauthenticated(externalID)
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest)
        return this.getAuthenticationManager().authenticate(authRequest)
    }

    @Nullable
    protected String obtainExternalID(HttpServletRequest request) {
        return request.getParameter('externalID')
    }

    protected void setDetails(HttpServletRequest request, ExternalIDAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
}
