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

import dueuno.commons.utils.KeyStoreUtils
import dueuno.elements.core.ApplicationService
import dueuno.elements.tenants.TenantService
import grails.gorm.multitenancy.CurrentTenant
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.security.KeyStore

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
@CurrentTenant
class KeyStoreService {

    ApplicationService applicationService
    TenantService tenantService

    void install() {
        byte[] password = KeyStoreUtils.generateKeyStorePassword()
        KeyStoreUtils.saveKeyStorePassword(password, keyStorePasswordPathname)
        applicationService.setAttribute(keyStorePasswordAttributeName, password)
    }

    void afterLogin() {
        byte[] password = KeyStoreUtils.loadKeyStorePassword(keyStorePasswordPathname)
        applicationService.setAttribute(keyStorePasswordAttributeName, password)
    }

    String encrypt(String value) {
        if (!value) {
            return ''
        }

        byte[] ksp = keyStorePasswordForTenant
        KeyStore ks = KeyStoreUtils.create(ksp)
        KeyStoreUtils.setKey(ks, ksp, 'value', value)
        return KeyStoreUtils.saveToString(ks, ksp)
    }

    String decrypt(String value) {
        if (!value) {
            return ''
        }

        byte[] ksp = keyStorePasswordForTenant
        KeyStore ks = KeyStoreUtils.loadFromString(value, ksp)
        return KeyStoreUtils.getKey(ks, ksp, 'value')
    }

    private byte[] getKeyStorePasswordForTenant() {
        return applicationService.getAttribute(keyStorePasswordAttributeName) as byte[]
    }

    private String getKeyStorePasswordPathname() {
        return tenantService.privateDir + applicationService.applicationName + '.key'
    }

    private String getKeyStorePasswordAttributeName() {
        return 'KEYSTORE_PASSWORD_' + tenantService.currentTenantId
    }
}
