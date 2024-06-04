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
package dueuno.elements.test

import dueuno.elements.components.TableRow
import dueuno.elements.contents.ContentCreate
import dueuno.elements.contents.ContentEdit
import dueuno.elements.contents.ContentList
import dueuno.elements.controls.*
import dueuno.elements.core.ElementsController

import java.time.LocalDate

class CrudDataServicesController implements ElementsController {

    PersonService personService

    def index() {
        def c = createContent(ContentList)
        c.with {
            table.with {
                filters.with {
                    addField(
                            class: TextField,
                            id: 'name',
                            cols: 12,
                    )
                }

                sortable = [
                        address: 'asc',
                        name: 'desc',
                ]
                columns = [
                        'company',
                        'employeeCount',
                        'name',
                        'picture',
                        'address',
                        'postcode',
                        'salary',
                        'salaryPerMonth',
                        'distanceKm',
                        'dateStart',
                        'dateEnd',
                        'active',
                ]
                includeValues = [
                        'company.employees',
                ]
                prettyPrinterProperties = [
                        salary: [highlightNegative: false, renderZero: '-'],
                        name: [renderMessagePrefix: true],
                ]

                body.eachRow { TableRow row, Map values ->
                }
            }
        }

        // QUERY
        //
        def filters = c.table.filterParams

        // VALUES
        //
        c.table.body = personService.listByCompanyOrNameLike(filters.company, filters.name ?: '', params)
        c.table.paginate = personService.count()

        display content: c
    }

    private buildForm(TPerson obj = null) {
        def c = obj
                ? createContent(ContentEdit)
                : createContent(ContentCreate)

        if (params.embedded) {
            c.header.addBackButton(animate: params.animate)
            c.header.backButton.with {
                addAction(action: 'pippo')
            }
            if (params.animate) {
                c.header.nextButton.animate = params.animate
            }
        }

        c.form.with {
            validate = TPerson
            addKeyField('embedded', 'BOOLEAN')

            //TODO: Fare in modo che l'azione riceva i dati convertiti in base al loro tipo
            addKeyField('selection', 'LIST', [[id: 1]])

            addField(
                    class: Select,
                    id: 'company',
                    optionsFromRecordset: TCompany.list(),
                    prettyPrinter: 'customCompanyPrinter',
                    onChange: 'onCompanyChange',
            )
            addField(
                    class: TextField,
                    id: 'companyName',
                    readonly: true,
            )
            addField(
                    class: TextField,
                    id: 'name',
            )
            addField(
                    class: Textarea,
                    id: 'address',
            )
            addField(
                    class: NumberField,
                    id: 'postcode',
            )
            addField(
                    class: MoneyField,
                    id: 'salary',
            )
            addField(
                    class: QuantityField,
                    id: 'distanceKm',
            )
            addField(
                    class: DateField,
                    id: 'dateStart',
                    min: LocalDate.now().minusDays(3),
                    max: LocalDate.now().plusDays(3),
            )
            addField(
                    class: DateField,
                    id: 'dateEnd',
            )
            addField(
                    class: Checkbox,
                    id: 'active',
            )
            addField(
                    class: Upload,
                    id: 'picture',
            )
        }

        if (obj) {
            c.form.values = obj
        }

        return c
    }

    def onCompanyChange() {
        def t = createTransition()
        def company = TCompany.get(params.company)
        t.set('companyName', company?.name)
        t.set('name', 'readonly', company)
        display transition: t
    }

    def create() {
        def c = buildForm()
        display content: c, modal: true
    }

    def edit(TPerson obj) {
        def c = buildForm(obj)
        display content: c, modal: true
    }

    def onCreate() {
        TPerson obj = personService.create(params)
        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        if (params.embedded) {
            display returnPoint(person: obj.id) + [modal: true]
        } else {
            display action: 'index'
        }
    }

    def onEdit() {
        TPerson obj = personService.update(params.id, params)
        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        display action: 'index'
    }

    def onDelete() {
        try {
            personService.delete(params.id)
            display action: 'index'

        } catch (e) {
            display exception: e
        }
    }

}
