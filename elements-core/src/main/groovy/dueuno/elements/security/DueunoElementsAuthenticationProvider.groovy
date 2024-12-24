package dueuno.elements.security

import dueuno.elements.core.SystemPropertyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component
class DueunoElementsAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    SystemPropertyService systemPropertyService

    @Override
    Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName()
        String password = authentication.getCredentials().toString()
        if ("pippo" != username || "pippo" != password) {
            return null
        }
        
        List<GrantedAuthority> authorities = new ArrayList<>()
        authorities.add(new SimpleGrantedAuthority('ROLE_USER'))
        DueunoElementsUserDetails principal = new DueunoElementsUserDetails(
                username,
                password,
                true,
                true,
                true,
                true,
                authorities,
                -1,
        )

        return new UsernamePasswordAuthenticationToken(principal, password, authorities)
    }

    @Override
    boolean supports(Class<?> authentication) {
        return authentication == UsernamePasswordAuthenticationToken
    }
}
