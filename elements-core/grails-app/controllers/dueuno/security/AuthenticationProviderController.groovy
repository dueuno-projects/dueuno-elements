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

import dueuno.elements.ElementsController
import dueuno.elements.components.Label
import dueuno.elements.components.Separator
import dueuno.elements.components.TableRow
import dueuno.elements.contents.ContentCreate
import dueuno.elements.contents.ContentEdit
import dueuno.elements.contents.ContentTable
import dueuno.elements.controls.Checkbox
import dueuno.elements.controls.NumberField
import dueuno.elements.controls.Select
import dueuno.elements.controls.TextField
import dueuno.core.ConnectionSourceService
import dueuno.elements.style.Color
import dueuno.elements.style.TextDefault
import dueuno.elements.style.TextTransform
import dueuno.tenants.TTenant
import dueuno.tenants.TenantService
import dueuno.utils.EnvUtils
import grails.plugin.springsecurity.annotation.Secured

/**
 * @author Gianluca Sartori
 */
@Secured(['ROLE_SUPERADMIN'])
class AuthenticationProviderController implements ElementsController {

    AuthenticationProviderService authenticationProviderService

    def index() {
        def c = createContent(ContentTable)
        c.table.with {
            filters.with {
            }
            sortable = [
                    sequence: 'asc',
            ]
            columns = [
                    'enabled',
                    'type',
                    'name',
                    'sequence',
            ]
            actions.removeTailAction()
            actions.addAction(action: 'moveUp', icon: 'fa-arrow-up')
            actions.addAction(action: 'moveDown', icon: 'fa-arrow-down')
            body.eachRow { TableRow row, Map values ->

            }
        }

        Map filters = c.table.filterParams
        c.table.body = authenticationProviderService.list(filters, c.table.fetchParams)
        c.table.paginate = authenticationProviderService.count(filters)

        display content: c
    }

    private buildForm(TAuthenticationProvider obj = null) {
        def c = obj
                ? createContent(ContentEdit)
                : createContent(ContentCreate)

        c.form.with {
            validate = TAuthenticationProvider
            addField(
                    class: Select,
                    id: 'type',
                    optionsFromEnum: AuthenticationProviderType,
                    renderTextPrefix: false,
                    readonly: true,
                    cols: 4,
            )
            addField(
                    class: TextField,
                    id: 'name',
                    readonly: true,
                    cols: 8,
            )
            addField(
                    class: NumberField,
                    id: 'sequence',
                    readonly: true,
                    cols: 4,
            )
            addField(
                    class: Checkbox,
                    id: 'enabled',
                    cols: 8,
            )
        }

        if (obj) {
            c.form.values = obj
        }

        return c
    }

    def edit() {
        def obj = authenticationProviderService.get(params.id)
        def c = buildForm(obj)
        display content: c, modal: true
    }

    def onEdit() {
        def obj = authenticationProviderService.update(params)
        if (obj.hasErrors()) {
            display errors: obj

        } else {
            display action: 'index'
        }
    }
}
