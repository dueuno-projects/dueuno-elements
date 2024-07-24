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

import dueuno.elements.components.Separator
import dueuno.elements.components.TableRow
import dueuno.elements.contents.ContentCreate
import dueuno.elements.contents.ContentEdit
import dueuno.elements.contents.ContentForm
import dueuno.elements.contents.ContentList
import dueuno.elements.controls.*
import dueuno.elements.core.ApplicationService
import dueuno.elements.core.ElementsController
import dueuno.elements.core.PrettyPrinterDecimalFormat
import dueuno.elements.core.SystemPropertyService
import dueuno.elements.tenants.TenantPropertyService
import dueuno.elements.tenants.TenantService
import grails.gorm.multitenancy.WithoutTenant
import grails.plugin.springsecurity.annotation.Secured

/**
 * Users Management
 *
 * @author Gianluca Sartori
 */
@WithoutTenant
@Secured(['ROLE_SECURITY'])
class UserController implements ElementsController {

    ApplicationService applicationService
    SecurityService securityService
    TenantService tenantService
    SystemPropertyService systemPropertyService
    TenantPropertyService tenantPropertyService

    def index() {
        Boolean isSuperAdmin = securityService.isSuperAdmin()
        List userColumns = isSuperAdmin ? ['tenant'] : []
        userColumns += [
                'username',
                'firstname',
                'lastname',
                'defaultGroup',
                'enabled',
                'admin',
                'system',
        ]

        def c = createContent(ContentList)

        c.header.removeBackButton()

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
                        id: 'username',
                        cols: isSuperAdmin ? 8 : 10,
                )
                addField(
                        class: Select,
                        id: 'enabled',
                        options: [true: 'enabled', false: 'disabled'],
                        noSelection: true,
                        search: false,
                        cols: 2,
                )
            }
            sortable = [
                    tenant: 'asc',
                    username: 'asc',
            ]
            keys = ['username']
            columns = userColumns

            body.eachRow { TableRow row, Map values ->
                values.admin = securityService.isAdmin(values.username)
                values.system = !values.deletable
                if (!values.deletable) {
                    row.actions.removeTailAction()
                }
            }
        }

        def filters = c.table.filterParams
        c.table.body = securityService.listAllUser(filters, params)
        c.table.paginate = securityService.countAllUser(filters)

        display content: c
    }

    private buildForm(TUser obj = null) {
        def c = obj
                ? createContent(ContentEdit)
                : createContent(ContentCreate)

        def mustRefresh = c in ContentCreate && controllerSession.createAndNew
        if (mustRefresh) {
            c.header.addBackButton(
                    action: 'index',
                    icon: 'fa-times',
                    text: '',
            )
        }

        c.form.with {
            validate = TUser

            if (securityService.isSuperAdmin()) {
                addField(
                        class: Select,
                        id: 'tenant',
                        optionsFromRecordset: tenantService.list(),
                        cols: 12,
                        search: false,
                        noSelection: false,
                        defaultValue: tenantService.default.id,
                        onLoad: 'onTenantChange',
                        onChange: 'onTenantChange',
                )
            }

            addField(
                    class: TextField,
                    id: 'username',
                    icon: 'fa-user',
                    cols: 6,
            )
            addField(
                    class: PasswordField,
                    id: 'password',
                    nullable: obj,
                    cols: 6,
            )
        }

        buildSensitiveDataForm(c)

        c.form.with {
            addField(
                    class: Separator,
                    id: 'authorizations',
                    cols: 12,
            )
            addField(
                    class: Select,
                    id: 'groups',
                    optionsFromRecordset: securityService.listGroup([hideUsers: true]),
                    search: false,
                    multiple: true,
                    cols: 12,
            )
            addField(
                    class: Select,
                    id: 'defaultGroup',
                    optionsFromRecordset: securityService.listGroup([hideUsers: true]),
                    search: false,
                    cols: 12,
            )
            addField(
                    class: NumberField,
                    id: 'sessionDuration',
                    prefix: 'min',
                    decimals: 0,
                    defaultValue: tenantPropertyService.getNumber('DEFAULT_SESSION_DURATION'),
                    cols: 6,
            )
            addField(
                    class: NumberField,
                    id: 'rememberMeDuration',
                    prefix: 'min',
                    decimals: 0,
                    defaultValue: tenantPropertyService.getNumber('DEFAULT_REMEMBER_ME_DURATION'),
                    cols: 6,
            )
            addField(
                    class: Checkbox,
                    id: 'admin',
                    text: '',
                    cols: 6,
            )
            addField(
                    class: Checkbox,
                    id: 'enabled',
                    text: '',
                    nullable: true,
                    cols: 6,
            )
        }

        buildPreferencesForm(c)

        c.form['language'].defaultValue = systemPropertyService.getString('DEFAULT_LANGUAGE')

        return c
    }

    private buildSensitiveDataForm(ContentForm c) {
        c.form.with {
            addField(
                    class: TextField,
                    id: 'firstname',
                    cols: 6,
            )
            addField(
                    class: TextField,
                    id: 'lastname',
                    cols: 6,
            )
            addField(
                    class: EmailField,
                    id: 'email',
                    cols: 6,
            )
            addField(
                    class: TelephoneField,
                    id: 'telephone',
                    cols: 6,
            )
            addField(
                    class: Textarea,
                    id: 'note',
                    maxSize: 2000,
                    cols: 12,
                    rows: 2,
            )
        }
    }

    private buildPreferencesForm(ContentForm c) {
        c.form.with {
            addField(
                    class: Separator,
                    id: 'preferences',
                    cols: 12,
            )
            addField(
                    class: Select,
                    id: 'language',
                    optionsFromList: applicationService.languages,
                    messagePrefix: 'default.language',
                    search: false,
                    cols: 6,
            )
            addField(
                    class: Select,
                    id: 'firstDaySunday',
                    options: [false: 'false', true: 'true'],
                    messagePrefix: 'default.firstDaySunday',
                    search: false,
                    cols: 6,
            )
            addField(
                    class: Select,
                    id: 'invertedMonth',
                    options: [false: 'false', true: 'true'],
                    messagePrefix: 'default.invertedMonth',
                    search: false,
                    cols: 6,
            )
            addField(
                    class: Select,
                    id: 'twelveHours',
                    options: [false: 'false', true: 'true'],
                    messagePrefix: 'default.twelveHours',
                    search: false,
                    cols: 6,
            )
            addField(
                    class: Select,
                    id: 'decimalFormat',
                    optionsFromEnum: PrettyPrinterDecimalFormat,
                    messagePrefix: 'default.decimalFormat',
                    search: false,
                    cols: 6,
            )
            addField(
                    class: Select,
                    id: 'prefixedUnit',
                    options: [false: 'false', true: 'true'],
                    messagePrefix: 'default.prefixedUnit',
                    search: false,
                    cols: 6,
            )
            addField(
                    class: Select,
                    id: 'symbolicCurrency',
                    options: [false: 'false', true: 'true'],
                    messagePrefix: 'default.symbolicCurrency',
                    search: false,
                    cols: 6,
            )
            addField(
                    class: Select,
                    id: 'symbolicQuantity',
                    options: [false: 'false', true: 'true'],
                    messagePrefix: 'default.symbolicQuantity',
                    search: false,
                    cols: 6,
            )
            addField(
                    class: NumberField,
                    id: 'fontSize',
                    defaultValue: 16,
                    cols: 6,
            )
            addField(
                    class: Checkbox,
                    id: 'animations',
                    defaultValue: true,
                    cols: 6,
            )
        }
    }

    def onTenantChange() {
        def rs = securityService.listGroup([tenant: params.tenant, hideUsers: true])
        def tenantGroups = Select.optionsFromRecordset(recordset: rs)
        def user = securityService.getUserByUsername(params.username)

        def t = createTransition()
        t.set('defaultGroup', 'options', tenantGroups)
        t.setValue('groups',  params.defaultGroup)
        t.set('groups', 'options', tenantGroups)

        if (user) {
            t.setValue('groups',  user.authorities*.id)
        }

        display transition: t
    }

    def create() {
        def c = buildForm()

        c.header.nextButton.action = 'onCreateAndClose'
        c.header.nextButton.text = 'user.onCreateAndClose'
        c.header.nextButton.icon = ''
        c.header.nextButton.addAction(action: 'onCreate', submit: ['form'])
        if (controllerSession.createAndNew) {
            c.header.nextButton.setDefaultAction(action: 'onCreate')
        } else {
            c.header.nextButton.setDefaultAction(action: 'onCreateAndClose')
        }

        c.form['enabled'].value = true

        def mustRefresh = c in ContentCreate && controllerSession.createAndNew
        display content: c, modal: true, closeButton: !mustRefresh
    }

    private normalizeInput(Map params) {
        params.tenant = tenantService.get(params.tenant) ?: securityService.currentTenant

        def defaultGroup = securityService.getGroup(params.defaultGroup)
        if (defaultGroup) params.defaultGroup = defaultGroup.name

        List groups = []
        for (groupId in params.groups) {
            groups.add(securityService.getGroup(groupId).name)
        }
        params.groups = groups
    }

    def onCreateAndClose() {
        controllerSession.createAndNew = false
        normalizeInput(params)
        def obj = securityService.createUser(params)
        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        display action: 'index'
    }

    def onCreate() {
        controllerSession.createAndNew = true
        normalizeInput(params)
        def obj = securityService.createUser(params)
        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        display action: 'create'
    }

    def edit() {
        def user = TUser.findByUsername(params.username)
        def c = buildForm(user)

        c.form['username'].readonly = true
        c.form['usernameField'].helpMessage = 'user.edit.username.help'

        c.form.values = user
        c.form['password'].value = null

        c.form['admin'].value = (user.authorities.find { it.name == SecurityService.GROUP_ADMINS } != null)
        c.form['admin'].readonly = !user.deletable
//        c.form.readonly = !user.deletable
        c.form['enabled'].readonly = !user.deletable

        c.form['groups'].value = user.authorities.collect { it.id }

        display content: c, modal: true
    }

    def onEdit() {
        normalizeInput(params)
        def obj = securityService.updateUserAndGroups(params)

        if (obj.hasErrors()) {
            display errors: obj

        } else {
            display action: 'index'
        }
    }

    def onDelete() {
        try {
            securityService.deleteUser(params.username)
            display action: 'index'

        } catch (e) {
            e.printStackTrace()
            display exception: e
        }
    }
}


