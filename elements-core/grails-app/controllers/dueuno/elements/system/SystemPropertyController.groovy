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

import dueuno.commons.utils.StringUtils
import dueuno.elements.components.Label
import dueuno.elements.components.TableRow
import dueuno.elements.contents.ContentEdit
import dueuno.elements.contents.ContentList
import dueuno.elements.controls.*
import dueuno.elements.core.ElementsController
import dueuno.elements.core.PropertyType
import dueuno.elements.core.SystemPropertyService
import dueuno.elements.core.TSystemProperty
import dueuno.elements.style.TextAlign
import dueuno.elements.style.TextDefault
import dueuno.elements.style.TextWrap
import grails.plugin.springsecurity.annotation.Secured

/**
 * @author Gianluca Sartori
 */
@Secured(['ROLE_SUPERADMIN'])
class SystemPropertyController implements ElementsController {

    SystemPropertyService systemPropertyService

    def index() {
        def c = createContent(ContentList)
        c.header.removeNextButton()
        c.table.with {
            filters.with {
                fold = false
                addField(
                        class: TextField,
                        id: 'find',
                        label: TextDefault.FIND,
                        cols: 8,
                )
                addField(
                        class: Select,
                        id: 'type',
                        optionsFromEnum: PropertyType,
                        noSelection: true,
                        cols: 2,
                )
                addField(
                        class: Select,
                        id: 'validation',
                        optionsFromList: ['error'],
                        search: false,
                        noSelection: true,
//                        messagePrefix: 'systemProperty.validation',
                        cols: 2,
                )
            }
            sortable = [
                    name: 'asc',
            ]
            columns = [
                    'issues',
                    'name',
                    'type',
                    'description',
//                    'value',
//                    'defaultValue',
            ]
//            prettyPrinterProperties = [
//                    value: [decimals: 5],
//                    defaultValue: [decimals: 5],
//            ]

            actions.removeTailAction()

            body.eachRow { TableRow row, Map values ->
//                row.cells.defaultValue.component.monospace = true
//                row.cells.defaultValue.textAlign = TextAlign.START
//                row.cells.value.component.monospace = true
//                row.cells.value.textAlign = TextAlign.START

                if (values.validation) {
                    row.textColor = '#cc0000'
                    row.cells.issues.icon = 'fa-circle-exclamation'
                }

//                String typeName = StringUtils.screamingSnakeToCamel(values.type as String)
//                values.value = values[typeName]
//                values.defaultValue = values[typeName + 'Default']

                String descriptionCode = "system.property.${values.name}"
                String description = messageOrBlank(descriptionCode)
                row.cells['description'].html = description ?: descriptionCode
                if (!description) {
                    row.cells['description'].textColor = secondaryBackgroundColor
                }

//                if (values.type == PropertyType.BOOL) {
//                    values.value = values.value ? 'TRUE' : 'FALSE'
//                }

//                if (values.type == PropertyType.PASSWORD) {
//                    values.value = '**********'
//                }
            }
        }

        def filters = c.table.filterParams
        c.table.body = systemPropertyService.list(filters, params)
        c.table.paginate = systemPropertyService.count(filters)

        display content: c
    }

    private buildForm(TSystemProperty obj) {
        def c = createContent(ContentEdit)

        c.form.with {
            validate = TSystemProperty
            grid = true

            String descriptionCode = "system.property.${obj.name}"
            String description = messageOrBlank(descriptionCode)
            if (description) {
                addField(
                        class: Label,
                        id: 'description',
                        html: description,
                        textWrap: TextWrap.SOFT_WRAP,
                        displayLabel: false,
                        border: true,
                        cols: 12,
                )
            }

            addField(
                    class: TextField,
                    id: 'name',
                    monospace: true,
                    readonly: true,
                    cols: 6,
            )

            addField(
                    class: Select,
                    id: 'type',
                    optionsFromEnum: PropertyType,
                    monospace: true,
                    readonly: true,
                    nullable: true,
                    cols: 6,
            )

            switch (obj.type) {
                case PropertyType.STRING:
                    if (obj.stringDefault) {
                        addField(
                                class: Textarea,
                                id: 'defaultValue',
                                monospace: true,
                                readonly: true,
                                rows: 2,
                                cols: 12,
                        )
                    }
                    addField(
                            class: Textarea,
                            id: 'value',
                            monospace: true,
                            rows: 2,
                            cols: 12,
                    )
                    break

                case PropertyType.PASSWORD:
                    addField(
                            class: PasswordField,
                            id: 'value',
                            helpMessage: 'tenantProperty.password.help',
                            cols: 12,
                    )
                    break

                case PropertyType.FILENAME:
                    if (obj.filenameDefault) {
                        addField(
                                class: TextField,
                                id: 'defaultValue',
                                monospace: true,
                                readonly: true,
                                cols: 12,
                        )
                    }
                    addField(
                            class: TextField,
                            id: 'value',
                            monospace: true,
                            onLoad: 'onValidate',
                            onChange: 'onValidate',
                            submit: ['form'],
                            cols: 12,
                    )
                    break

                case PropertyType.DIRECTORY:
                    if (obj.directoryDefault) {
                        addField(
                                class: TextField,
                                id: 'defaultValue',
                                monospace: true,
                                readonly: true,
                                cols: 12,
                        )
                    }
                    addField(
                            class: TextField,
                            id: 'value',
                            monospace: true,
                            onLoad: 'onValidate',
                            onChange: 'onValidate',
                            submit: ['form'],
                            cols: 12,
                    )
                    break

                case PropertyType.URL:
                    if (obj.urlDefault) {
                        addField(
                                class: TextField,
                                id: 'defaultValue',
                                monospace: true,
                                readonly: true,
                                cols: 12,
                        )
                    }
                    addField(
                            class: TextField,
                            id: 'value',
                            monospace: true,
                            onLoad: 'onValidate',
                            onChange: 'onValidate',
                            submit: ['form'],
                            cols: 12,
                    )
                    break

                case PropertyType.NUMBER:
                    if (obj.numberDefault) {
                        addField(
                                class: NumberField,
                                id: 'defaultValue',
                                monospace: true,
                                decimals: 5,
                                readonly: true,
                                cols: 12,
                        )
                    }
                    addField(
                            class: NumberField,
                            id: 'value',
                            monospace: true,
                            decimals: 5,
                            cols: 12,
                    )
                    break

                case PropertyType.BOOL:
                    if (obj.boolDefault) {
                        addField(
                                class: Checkbox,
                                id: 'defaultValue',
                                readonly: true,
                                label: '',
                                cols: 12,
                        )
                    }
                    addField(
                            class: Checkbox,
                            id: 'value',
                            label: '',
                            cols: 12,
                    )
                    break

                case PropertyType.DATETIME:
                    if (obj.datetimeDefault) {
                        addField(
                                class: DateTimeField,
                                id: 'defaultValue',
                                readonly: true,
                                cols: 12,
                        )
                    }
                    addField(
                            class: DateTimeField,
                            id: 'value',
                            cols: 12,
                    )
                    break

                case PropertyType.DATE:
                    if (obj.dateDefault) {
                        addField(
                                class: DateField,
                                id: 'defaultValue',
                                readonly: true,
                                cols: 12,
                        )
                    }
                    addField(
                            class: DateField,
                            id: 'value',
                            cols: 12,
                    )
                    break

                case PropertyType.TIME:
                    if (obj.timeDefault) {
                        addField(
                                class: TimeField,
                                id: 'defaultValue',
                                readonly: true,
                                cols: 12,
                        )
                    }
                    addField(
                            class: TimeField,
                            id: 'value',
                            cols: 12,
                    )
                    break
            }
        }

        if (obj) {
            c.form.values = obj
            c.form.value.value = systemPropertyService.getValue(obj.type, obj.name, true)

            if (obj.type == PropertyType.PASSWORD) {
                c.form.value.value = ''
            }
        }

        return c
    }

    def onValidate() {
        def t = createTransition()

        def validation
        switch (params.type as PropertyType) {
            case PropertyType.FILENAME:
                validation = systemPropertyService.validateFilename(params.value)
                break

            case PropertyType.DIRECTORY:
                validation = systemPropertyService.validateDirectory(params.value)
                break

            case PropertyType.URL:
                validation = systemPropertyService.validateUrl(params.value)
                break
        }

        t.set('valueField', 'error', validation)

        display transition: t
    }

    def edit() {
        def obj = systemPropertyService.get(params.id)
        def c = buildForm(obj)
        display content: c, modal: true
    }

    def onEdit() {
        systemPropertyService.setValue(params.type as PropertyType, params.name, params.value)
        display action: 'index'
    }
}
