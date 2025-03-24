package dueuno.elements.test

import dueuno.elements.core.ElementsController
import dueuno.elements.security.SecurityService

class KeyPressController implements ElementsController{

    SecurityService securityService

    def onKeyPress() {
        String externalId = params._21KeyPressed
        def user = securityService.getUserByExternalId(externalId)
        if (user) {
            display controller: 'authentication', action: 'logout'
        } else {
            display
        }
    }
}
