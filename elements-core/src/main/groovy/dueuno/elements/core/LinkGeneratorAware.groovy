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
package dueuno.elements.core

import grails.web.mapping.LinkGenerator
import groovy.transform.CompileStatic

/**
 * Helper trait to generate links from any class
 *
 * @author Gianluca Sartori
 */

@CompileStatic
trait LinkGeneratorAware {

    private LinkGenerator getLinkGenerator() {
        return Elements.getBean("grailsLinkGenerator") as LinkGenerator
    }

    /**
     * Wrapper to Grails g.link() (See: Grails g.link())
     * @return a URL built from the specified params
     */
    String link(Map args) {
        return getLinkGenerator().link(args)
    }

    /**
     * Returns the absolute (Eg. http://..) application URL as configured on build.gradle
     * @return the absolute (Eg. http://..) application URL as configured on build.gradle
     */
    String linkApplication(Boolean absolute = false) {
        String result = getLinkGenerator().link(uri: '/', absolute: absolute)
        return result
    }

    /**
     * Returns the relative URL to a static resource from a filesystem path defined in a System Property
     * @return the relative URL to a static resource from a filesystem path defined in a System Property
     *
     * @param root The name of the system property specifying an absolute path in the filesystem
     * @param pathname The name of the file to serve
     */
    String linkResource(String root, String pathname, Boolean absolute = false, Boolean reload = true) {
        return generateResourceLink('system', '', root, pathname, absolute, reload)
    }

    /**
     * Returns the relative URL to a static resource from a filesystem path defined in '${tenantId}_TENANT_PUBLIC_DIR'
     * @return the relative URL to a static resource from a filesystem path defined in '${tenantId}_TENANT_PUBLIC_DIR'
     *
     * @param pathname The name of the file to serve
     */
    String linkPublicResource(String pathname, Boolean absolute = false, Boolean reload = true) {
        return generateResourceLink('currentTenant', '', '', pathname, absolute, reload)
    }

    /**
     * Returns the relative URL to a static resource from a filesystem path defined in '${tenantId}_TENANT_PUBLIC_DIR'
     * @return the relative URL to a static resource from a filesystem path defined in '${tenantId}_TENANT_PUBLIC_DIR'
     *
     * @param tenantId The id of the tenant
     * @param pathname The name of the file to serve
     */
    String linkPublicResource(String tenantId, String pathname, Boolean absolute = false, Boolean reload = true) {
        return generateResourceLink('tenant', tenantId, '', pathname, absolute, reload)
    }

    private String generateResourceLink(String action, String tenantId, String root, String pathname, Boolean absolute, Boolean reload) {
        String request = "downloadResource/${action}?tenantId=${tenantId}&root=${root}&pathname=${pathname}${reload ? '&' + System.currentTimeMillis() : '' }"
        return linkApplication(absolute) + request
    }
}
