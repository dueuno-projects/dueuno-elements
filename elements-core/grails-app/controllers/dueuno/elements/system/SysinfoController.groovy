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
package dueuno.elements.system

import dueuno.elements.components.Label
import dueuno.elements.contents.ContentForm
import dueuno.elements.controls.TextField
import dueuno.elements.core.ElementsController
import dueuno.elements.core.SystemInfoService
import dueuno.elements.style.TextWrap
import dueuno.elements.utils.EnvUtils
import grails.plugin.springsecurity.annotation.Secured

/**
 * @author Gianluca Sartori
 */
@Secured(['ROLE_SUPERADMIN'])
class SysinfoController implements ElementsController {

    SystemInfoService systemInfoService

    def index() {

        def c = createContent(ContentForm)
        c.header.with {
            removeBackButton()
            removeNextButton()
        }

        c.form.with {
            addField(
                    class: TextField,
                    id: 'environment',
                    readonly: true,
                    cols: 4,
            )
            addField(
                    class: TextField,
                    id: 'applicationVersion',
                    readonly: true,
                    cols: 4,
            )
            addField(
                    class: TextField,
                    id: 'coreVersion',
                    readonly: true,
                    cols: 4,
            )
            addField(
                    class: Label,
                    id: 'systemData',
                    textWrap: TextWrap.LINE_WRAP,
                    border: true,
                    cols: 12,
            )
        }

        // DATA
        //
        String systemData = ''
        systemInfoService.info.each { key, value ->
            if (key != 'coreVersion') systemData += "$key: $value\n"
        }

        c.form.values = [
                environment       : message('default.env.' + EnvUtils.currentEnvironment),
                browser           : systemInfoService.info.browser,
                applicationVersion: systemInfoService.info.appVersion,
                coreVersion       : systemInfoService.info.coreVersion,
        ]

        c.form['systemData'].text = systemData

        // RENDERING
        //
        display content: c, modal: true
    }
}
