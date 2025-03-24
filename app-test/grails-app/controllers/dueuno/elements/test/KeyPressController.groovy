package dueuno.elements.test

import dueuno.elements.core.ElementsController
import dueuno.elements.security.SecurityService

class KeyPressController implements ElementsController{

    SecurityService securityService

    def onKeyPress() {
        String externalId = params._21KeyPressed
        securityService.executeLogoutIfExternalId(externalId)
        display controller: 'authentication', action: 'login', direct: true
    }
}
