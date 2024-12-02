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
package dueuno.elements.components

import dueuno.elements.contents.ContentForm
import dueuno.elements.core.ApplicationService
import dueuno.elements.core.ElementsController
import dueuno.elements.security.SecurityService
import dueuno.elements.style.TextAlign
import dueuno.elements.style.TextWrap
import dueuno.elements.tenants.TenantPropertyService
import grails.plugin.springsecurity.annotation.Secured

/**
 * The Shell
 *
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */
@Secured(['IS_AUTHENTICATED_REMEMBERED'])
class ShellController implements ElementsController {

    ApplicationService applicationService
    TenantPropertyService tenantPropertyService
    ShellService shellService
    SecurityService securityService

    def index() {
        shellService.shell.content = shellService.shell.home
        display page: shellService.shell
    }

    def credits() {
        def c = createContent(ContentForm)
        c.header.removeNextButton()

        String logoImage = tenantPropertyService.getString('LOGIN_LOGO')

        c.form.with {
            addField(
                    class: Label,
                    id: 'logo',
                    html: """<img width="50%" src="${logoImage}" />""",
                    textAlign: TextAlign.CENTER,
                    displayLabel: false,
            )
            addField(
                    class: Label,
                    id: 'title',
                    html: """<div class="m-4">${message('app.credits.text')}</div>""",
                    textWrap: TextWrap.SOFT_WRAP,
                    textAlign: TextAlign.CENTER,
                    displayLabel: false,
            )

            Integer i = 0
            for (item in applicationService.credits) {
                addField(
                        class: Label,
                        id: "credits${i}",
                        html: """${item.key}<br/><strong>${item.value.join('</strong><br/><strong>')}</strong>""",
                        textAlign: TextAlign.CENTER,
                        label: '',
                )
                i++
            }
        }

        display content: c, modal: true
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def toggleDevHints() {
        devDisplayHints = !devDisplayHints
        display controller: 'shell', direct: true
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def toggleClientLogs() {
        Boolean logs = tenantPropertyService.getBoolean('LOG_DEBUG')
        tenantPropertyService.setBoolean('LOG_DEBUG', !logs)
        display controller: 'shell', direct: true
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def switchLanguage() {
        currentLanguage = (String) params.id
        securityService.saveCurrentUserLanguage()
        display controller: 'shell', direct: true
    }
}
