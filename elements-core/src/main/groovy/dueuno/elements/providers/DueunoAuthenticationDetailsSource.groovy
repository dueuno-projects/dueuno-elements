package dueuno.elements.providers

import groovy.transform.CompileStatic
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource

import javax.servlet.http.HttpServletRequest

@CompileStatic
class DueunoAuthenticationDetailsSource extends WebAuthenticationDetailsSource {

    @Override
    WebAuthenticationDetails buildDetails(HttpServletRequest context) {
        def details = new DueunoAuthenticationDetails(context)

        String externalID = obtainExternalID(context)
        details.externalID = externalID

        return details
    }

    /**
     * Get the External ID from the request.
     * @param request
     * @return
     */
    private static String obtainExternalID(HttpServletRequest request) {
        return request.getParameter('externalID')
    }
}
