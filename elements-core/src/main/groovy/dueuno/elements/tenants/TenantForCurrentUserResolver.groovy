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
package dueuno.elements.tenants

import dueuno.elements.security.SecurityService
import grails.core.GrailsApplication
import grails.util.Holders
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.multitenancy.TenantResolver
import org.grails.datastore.mapping.multitenancy.exceptions.TenantNotFoundException
import org.springframework.beans.factory.annotation.Autowired

/**
 * @author Gianluca Sartori
 */

@Slf4j
class TenantForCurrentUserResolver implements TenantResolver {

    // Do not change the below declarations, otherwise it wont work with "grails run-app"
    //
    @Autowired
    private SecurityService securityService

    @Autowired
    private TenantService tenantService

    @Override
    Serializable resolveTenantIdentifier() throws TenantNotFoundException {

        if (!securityService) {
            // This hack is here because the services don't get automatically injected (it's a mistery)
            GrailsApplication grailsApplication = Holders.grailsApplication
            securityService = grailsApplication.mainContext.getBean('securityService')
            tenantService = grailsApplication.mainContext.getBean('tenantService')
        }

        if (securityService.currentUser) {
            return securityService.currentUser.tenant.tenantId

        } else {
            return tenantService.defaultTenantId
        }
    }
}
