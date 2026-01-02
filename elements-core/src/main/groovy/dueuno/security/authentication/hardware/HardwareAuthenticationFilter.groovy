package dueuno.security.authentication.hardware

import groovy.transform.CompileStatic
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter

@CompileStatic
class HardwareAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    HardwareAuthenticationFilter() {
        super('/authentication/hardware')
    }

    @Override
    Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) {

        String token = request.getParameter('token')
        if (!token) {
            throw new AuthenticationServiceException("Login failed: no token detected.")
        }

        HardwareAuthenticationToken authRequest =
                new HardwareAuthenticationToken(token)

        return authenticationManager.authenticate(authRequest)
    }
}
