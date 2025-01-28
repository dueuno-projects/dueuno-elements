/*
 * Copyright 2021 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dueuno.elements.security

import dueuno.elements.core.ElementsController
import dueuno.elements.core.PageService
import dueuno.elements.pages.Login
import dueuno.elements.tenants.TenantPropertyService
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

/**
 * Dueuno Security
 *
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */
@Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
class AuthenticationController implements ElementsController {

    SecurityService securityService
    TenantPropertyService tenantPropertyService

    def login() {
        if (securityService.isLoggedIn()) {
            String shellUrlMapping = tenantPropertyService.getString('SHELL_URL_MAPPING', true)
            String landingPage = securityService.landingPage

            redirect uri: landingPage ?: shellUrlMapping ?: '/'
            return
        }

        def loginArgs = [
                backgroundImage    : tenantPropertyService.getString('LOGIN_BACKGROUND_IMAGE', true),
                logoImage          : tenantPropertyService.getString('LOGIN_LOGO', true),
                rememberMe         : tenantPropertyService.getBoolean('LOGIN_REMEMBER_ME', true),
                autocomplete       : tenantPropertyService.getBoolean('LOGIN_AUTOCOMPLETE', true),
                copy               : tenantPropertyService.getString('LOGIN_COPY', true),
                registerUrl        : tenantPropertyService.getString('LOGIN_REGISTRATION_URL', true),
                passwordRecoveryUrl: tenantPropertyService.getString('LOGIN_PASSWORD_RECOVERY_URL', true),
        ]
        def login = createPage(Login, loginArgs)

        display page: login
    }

    @Secured(['ROLE_USER'])
    def afterLogin() {
        securityService.executeAfterLogin()

        if (session[SecurityService.DENY_AUTHORIZATION_MESSAGE]) {
            forward action: 'logout'
            return
        }

        ///////////////////////////////////////
        // IF YOU'RE HERE THE USER LOGGED IN //
        ///////////////////////////////////////

        // Loading user setting
        TUser user = securityService.currentUser
        decimalFormat = user.decimalFormat
        prefixedUnit = user.prefixedUnit
        symbolicCurrency = user.symbolicCurrency
        symbolicQuantity = user.symbolicQuantity
        invertedMonth = user.invertedMonth
        twelveHours = user.twelveHours
        firstDaySunday = user.firstDaySunday
        fontSize = user.fontSize
        animations = user.animations

        // We redirect the user to the configured location
        String shellUrlMapping = tenantPropertyService.getString('SHELL_URL_MAPPING', true)
        String landingPage = securityService.landingPage

        if (params.ajax) { // Default login
            def message = [
                    login   : true,
                    success : true,
                    redirect: landingPage ?: shellUrlMapping ?: '/',
            ]
            render message as JSON

        } else { // Legacy, keep it just in case
            redirect uri: landingPage ?: shellUrlMapping ?: '/'
        }
    }

    def logout() {
        String logoutLandingPage = tenantPropertyService.getString('LOGOUT_LANDING_URL', true)
        String shellUrlMapping = tenantPropertyService.getString('SHELL_URL_MAPPING', true)

        securityService.executeAfterLogout()

        redirect uri: logoutLandingPage ?: shellUrlMapping ?: '/'
    }

    def afterLogout() {
        // Serve a gestire il DENY_AUTHORIZATION_MESSAGE ovvero l'avviso da mostrare nel caso l'utente non sia autorizzato
        // ad accedere per motivi legati all'applicazione
        if (session[SecurityService.DENY_AUTHORIZATION_MESSAGE]) {
            def message = [customError: session[SecurityService.DENY_AUTHORIZATION_MESSAGE]]
            render message as JSON

        } else {
            String logoutLandingPage = tenantPropertyService.getString('LOGOUT_LANDING_URL', true)
            String shellUrlMapping = tenantPropertyService.getString('SHELL_URL_MAPPING', true)
            redirect uri: logoutLandingPage ?: shellUrlMapping ?: '/'
        }

//        securityService.executeLogout()
    }

    def err403() {
        withFormat {
            json {
                render status: 403
            }
            '*' {
                render view: '/dueuno/elements/authentication/403'
            }
        }
    }

}
