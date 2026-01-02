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
package dueuno.security

import grails.compiler.GrailsCompileStatic
import org.grails.datastore.gorm.GormEntity

/**
 * @author Gianluca Sartori
 */

@GrailsCompileStatic
class TAuthenticationProvider implements GormEntity {
    Long id

    String name
    AuthenticationProviderType type
    Integer sequence
    Boolean enabled

    // LDAP
    String ldapUrl
    String ldapBaseDn
    String ldapUserDnPattern
    String ldapManagerDn
    String ldapManagerPassword
    String ldapGroupSearchBase
    String ldapSearchFilter

    // OAuth2 / OIDC
    String clientId
    String clientSecret
    String authorizationUri
    String tokenUri
    String userInfoUri
    String redirectUri
    String scope

    String issuerUri           // OIDC
    String tenantId            // Microsoft Entra
    String jwkSetUri

    String usernameAttribute   // es. preferred_username
    String emailAttribute

    static constraints = {
        name unique: true

        // LDAP
        ldapUrl nullable: true
        ldapBaseDn nullable: true
        ldapUserDnPattern nullable: true
        ldapManagerDn nullable: true
        ldapManagerPassword nullable: true
        ldapGroupSearchBase nullable: true
        ldapSearchFilter nullable: true

        // OAuth2
        clientId nullable: true
        clientSecret nullable: true
        authorizationUri nullable: true
        tokenUri nullable: true
        userInfoUri nullable: true
        redirectUri nullable: true
        scope nullable: true
        issuerUri nullable: true
        tenantId nullable: true
        jwkSetUri nullable: true
        usernameAttribute nullable: true
        emailAttribute nullable: true
    }
}
