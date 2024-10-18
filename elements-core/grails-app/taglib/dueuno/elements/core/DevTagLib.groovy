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

import dueuno.elements.tenants.TenantPropertyService
import dueuno.elements.utils.EnvUtils

/**
 * @author Gianluca Sartori
 */
class DevTagLib implements WebRequestAware {

    TenantPropertyService tenantPropertyService

    static namespace = "dev"

    def ifDevelopment = { attrs, body ->
        if (EnvUtils.isDevelopment()) {
            out << body()
        }
    }

    def ifDisplayHints = { attrs, body ->
        if (devDisplayHints) {
            out << body()
        }
    }

    def displayHints = { args ->
        String result = devDisplayHints ? 'true' : 'false'
        out << result
    }

    def logError = { args ->
        String result = tenantPropertyService.getBoolean('LOG_ERROR') ? 'true' : 'false'
        out << result
    }

    def logDebug = { args ->
        String result = tenantPropertyService.getBoolean('LOG_DEBUG') ? 'true' : 'false'
        out << result
    }

    def logTrace = { args ->
        String result = tenantPropertyService.getBoolean('LOG_TRACE') ? 'true' : 'false'
        out << result
    }

}
