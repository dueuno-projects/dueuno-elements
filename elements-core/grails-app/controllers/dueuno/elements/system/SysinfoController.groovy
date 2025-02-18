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
import dueuno.elements.components.Table
import dueuno.elements.components.TableRow
import dueuno.elements.contents.ContentForm
import dueuno.elements.controls.TextField
import dueuno.elements.core.ElementsController
import dueuno.elements.core.SystemInfoService
import dueuno.elements.style.TextStyle
import dueuno.elements.style.TextWrap
import dueuno.elements.style.VerticalAlign
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
                    textStyle: TextStyle.MONOSPACE,
                    readonly: true,
                    cols: 4,
            )
            addField(
                    class: TextField,
                    id: 'applicationVersion',
                    textStyle: TextStyle.MONOSPACE,
                    readonly: true,
                    cols: 4,
            )
            addField(
                    class: TextField,
                    id: 'coreVersion',
                    textStyle: TextStyle.MONOSPACE,
                    readonly: true,
                    cols: 4,
            )
        }

        c.form.values = [
                environment       : message('default.env.' + EnvUtils.currentEnvironment),
                browser           : systemInfoService.info.browser,
                applicationVersion: systemInfoService.info.appVersion,
                coreVersion       : systemInfoService.info.coreVersion,
        ]

        List<Map> systemData = []
        systemInfoService.info.each { String key, Object value ->
            if (key != 'coreVersion') systemData << [key: key, value: value as String]
        }

        def table = c.addComponent(Table)
        table.with {
            columns = [
                    'key',
                    'value',
            ]
            hasHeader = false
            rowActions = false
            body.eachRow { TableRow row, Map values ->
                row.textStyle = TextStyle.MONOSPACE
                row.cells['value'].textWrap = TextWrap.SOFT_WRAP
            }
            body = systemData
        }

        display content: c, modal: true, wide: true
    }
}
