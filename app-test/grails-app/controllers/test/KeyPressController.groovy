package test

import dueuno.elements.ElementsController
import dueuno.security.SecurityService

class KeyPressController implements ElementsController {

    SecurityService securityService

    def onKeyPress() {
        String token = keyPressed
        def user = securityService.getUserByHardwareToken(token)
        if (user) {
            display controller: 'authentication', action: 'logout'
            return
        }

        display
    }
}
