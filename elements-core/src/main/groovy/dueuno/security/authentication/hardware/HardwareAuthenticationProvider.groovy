package dueuno.security.authentication.hardware

import dueuno.elements.security.SecurityService
import dueuno.elements.security.TUser
import groovy.transform.CompileStatic
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication

@CompileStatic
class HardwareAuthenticationProvider implements AuthenticationProvider {

    SecurityService securityService

    @Override
    Authentication authenticate(Authentication authentication) {

        HardwareAuthenticationToken hardware = authentication as HardwareAuthenticationToken
        TUser user = securityService.getUserByHardwareToken(hardware.token)
        if (!user) {
            throw new BadCredentialsException("Invalid token.")
        }

        return new HardwareAuthenticationToken(
                user,
                user.authorities
        )
    }

    @Override
    boolean supports(Class<?> authentication) {
        HardwareAuthenticationToken.isAssignableFrom(authentication)
    }
}