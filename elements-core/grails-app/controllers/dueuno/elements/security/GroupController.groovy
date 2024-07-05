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

import dueuno.elements.components.ShellService
import dueuno.elements.components.TableRow
import dueuno.elements.contents.ContentCreate
import dueuno.elements.contents.ContentEdit
import dueuno.elements.contents.ContentList
import dueuno.elements.controls.MultipleCheckbox
import dueuno.elements.controls.Select
import dueuno.elements.controls.TextField
import dueuno.elements.core.Menu
import dueuno.elements.core.ElementsController
import dueuno.elements.tenants.TenantService
import grails.gorm.multitenancy.WithoutTenant
import grails.plugin.springsecurity.annotation.Secured

/**
 * Group Management
 *
 * @author Gianluca Sartori
 */
@WithoutTenant
@Secured(['ROLE_SECURITY'])
class GroupController implements ElementsController {

    SecurityService securityService
    TenantService tenantService
    ShellService shellService

    def index() {
        Boolean isSuperAdmin = securityService.isSuperAdmin()
        List cols = isSuperAdmin ? ['tenant'] : []
        cols += [
                'name',
                'landingPage',
                'system',
        ]
        for (authority in (securityService.listAuthority())) {
            cols.add(authority)
        }

        def c = createContent(ContentList)
        c.table.with {
            filters.with {
                fold = false
                if (isSuperAdmin) {
                    addField(
                            class: Select,
                            id: 'tenant',
                            optionsFromRecordset: tenantService.list(),
                            cols: 2,
                    )
                }
                addField(
                        class: TextField,
                        id: 'name',
                        cols: isSuperAdmin ? 10 : 12,
                )
            }

            sortable = [
                    tenant: 'asc',
                    name: 'asc',
            ]
            keys = ['id']
            columns = cols
            prettyPrinters = [
                    landingPage: 'LANDING_PAGE',
            ]

            body.eachRow { TableRow row, Map values ->
                values.system = !values.deletable
                if (!values.deletable) {
                    row.actions.removeTailAction()
                }
            }
        }

        def filters = c.table.filterParams
        def rows = []
        def groups = securityService.listGroup(filters, params)
        for (group in groups) {
            def cells = [
                    id         : group.id,
                    tenant     : group.tenant,
                    name       : group.name,
                    landingPage: group.landingPage,
                    deletable     : group.deletable,
            ]
            for (role in TRole.findAll()) {
                cells[role.authority] = (group.authorities.any { it.authority == role.authority }) ?: null
            }
            rows.add(cells)
        }

        c.table.body = rows
        c.table.paginate = securityService.countGroup(filters)

        display content: c
    }

    private buildForm(TRoleGroup obj = null) {
        Boolean isSuperAdmin = securityService.isSuperAdmin()
        def c = obj
                ? createContent(ContentEdit)
                : createContent(ContentCreate)

        c.form.with {
            validate = TRoleGroup
            if (isSuperAdmin) {
                addField(
                        class: Select,
                        id: 'tenant',
                        optionsFromRecordset: tenantService.list(),
                        search: false,
                        noSelection: false,
                        defaultValue: tenantService.default.id,
                )
            }
            addField(
                    class: TextField,
                    id: 'name',
                    icon: 'fa-shield',
            )
            addField(
                    class: Select,
                    id: 'landingPage',
                    optionsFromRecordset: getLandingPages(),
                    prettyPrinter: 'LANDING_PAGE',
                    keys: ['controller'],
            )
        }
        return c
    }

    private List<String> getLandingPages() {
        List<Menu> results = []

        for (item in shellService.shell.menu.items) {
            if (item.items) {
                results.addAll(item.items)
            } else {
                results.add(item)
            }
        }

        return results
    }

    def create() {
        def c = buildForm()
        c.form.addField(
                class: MultipleCheckbox,
                id: 'authorities',
                optionsFromList: securityService.listAuthority(),
        )
        display content: c, modal: true
    }

    def onCreate() {
        params.tenant = tenantService.get(params.tenant) ?: securityService.currentTenant
        def obj = securityService.createGroup(params)
        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        display action: 'index'
    }

    def edit() {
        def obj = securityService.getGroup(params.id)
        def c = buildForm(obj)

        if (obj.name == SecurityService.GROUP_USERS) {
            c.form['name'].readonly = true
            c.form.addField(
                    class: MultipleCheckbox,
                    id: 'authorities',
                    optionsFromList: ['ROLE_USER'],
                    messagePrefix: controllerName,
                    readonly: true,
            )
        } else {
            c.form.addField(
                    class: MultipleCheckbox,
                    id: 'authorities',
                    optionsFromList: securityService.listAuthority(),
                    messagePrefix: controllerName,
            )
        }

        c.form.values = obj
        c.form['authorities'].value = obj.authorities.collect { it.authority }
        display content: c, modal: true
    }

    def onEdit() {
        params.tenant = tenantService.get(params.tenant) ?: securityService.currentTenant
        def obj = securityService.updateGroup(params)
        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        display action: 'index'
    }

    def onDelete() {
        try {
            securityService.deleteGroup(params.id)
            display action: 'index'
            
        } catch (Exception e) {
            display exception: e
        }
    }

}
