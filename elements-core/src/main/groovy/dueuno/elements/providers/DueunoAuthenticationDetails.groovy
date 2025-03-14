package dueuno.elements.providers

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import org.springframework.security.web.authentication.WebAuthenticationDetails

import javax.servlet.http.HttpServletRequest

@Canonical
@CompileStatic
class DueunoAuthenticationDetails extends WebAuthenticationDetails {
    String externalID

    DueunoAuthenticationDetails(HttpServletRequest request) {
        super(request)
    }
}
