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

import groovy.transform.CompileStatic

/**
 * Enumeration of supported authentication provider types.
 *
 * Each enum value represents a distinct authentication mechanism
 * that can be enabled, ordered and configured at runtime.
 *
 * The {@code providerName} is used to reference the corresponding
 * Spring Security AuthenticationProvider bean.
 *
 * @author Gianluca Sartori
 */
@CompileStatic
enum AuthenticationProviderType {

    /**
     * Database-backed authentication using username and password.
     */
    DAO('daoAuthenticationProvider'),

    /**
     * Authentication based on a physical or virtual hardware token.
     * The token is usually read as keyboard input and matched against
     * a stored identifier.
     */
    HARDWARE('hardwareAuthenticationProvider'),

    /**
     * Authentication using an LDAP directory or Active Directory.
     */
    LDAP('ldapAuthProvider'),

    /**
     * Authentication delegated to external OAuth2 / OpenID Connect
     * identity providers such as Google, Microsoft Entra, or Facebook.
     */
    OAUTH2('oauth2AuthenticationProvider')

    /**
     * Name of the Spring Security AuthenticationProvider bean
     * associated with this authentication type.
     */
    final String providerName

    /**
     * Creates a new authentication provider type.
     *
     * @param providerName the Spring bean name of the corresponding
     *                     AuthenticationProvider
     */
    AuthenticationProviderType(String providerName) {
        this.providerName = providerName
    }
}
