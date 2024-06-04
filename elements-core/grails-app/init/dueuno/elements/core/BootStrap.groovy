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

import dueuno.elements.components.ShellService
import dueuno.elements.security.SecurityService
import dueuno.elements.types.Money
import dueuno.elements.types.Quantity
import dueuno.elements.types.Types

/**
 * @author Gianluca Sartori
 */
class BootStrap {

    ApplicationService applicationService
    ShellService shellService
    SecurityService securityService

    def init = { servletContext ->

        applicationService.onPluginInstall { String tenantId ->
            shellService.install(tenantId)
        }

        applicationService.beforeInit {
            Types.register('MONEY', Money)
            Types.register('QUANTITY', Quantity)
            
            securityService.init()
        }

        applicationService.afterInit {
            securityService.registerFeatures()
        }
    }

    def destroy = {
        //no-op
    }

}
