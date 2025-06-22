package dueuno.elements.test

import dueuno.elements.core.ElementsController
import dueuno.elements.security.SecurityService

class KeyPressController implements ElementsController {

    SecurityService securityService

    def onKeyPress() {
        String externalId = keyPressed
        def user = securityService.getUserByExternalId(externalId)
        if (user) {
            display controller: 'authentication', action: 'logout'
            return
        }

        display
    }
}
