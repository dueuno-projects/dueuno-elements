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

import dueuno.elements.components.KeyPress
import dueuno.elements.components.TableRow
import dueuno.elements.contents.ContentCreate
import dueuno.elements.contents.ContentEdit
import dueuno.elements.contents.ContentForm
import dueuno.elements.contents.ContentList
import dueuno.elements.controls.*
import dueuno.elements.core.ApplicationService
import dueuno.elements.core.ElementsController
import dueuno.elements.style.TextDefault
import dueuno.elements.style.TextWrap
import dueuno.elements.style.VerticalAlign
import dueuno.elements.types.QuantityService
import grails.gorm.multitenancy.CurrentTenant
import grails.plugin.springsecurity.annotation.Secured

import java.time.LocalDate

@CurrentTenant
class CrudController implements ElementsController {

    ApplicationService applicationService
    QuantityService quantityService

    def index() {

        applicationService.registerTransformer('TRANSFORM_ME') { TCompany value ->
            return value.name.toUpperCase()
        }

//        throw new Exception("ATTENZIONE!!!")

        def c = createContent(ContentList)
        c.addComponent(
                class: KeyPress,
                id: 'pippo',
                action: 'onKeyPress',
                triggerKey: '',
        )

        c.title = 'Runtime "content" title'
        c.with {
            header.sticky = false
            header.text = 'Runtime "content.header" title'
            header.addBackButton().with {
                addAction(action: 'pippo')
            }
            header.nextButton.addAction(action: 'onCreateRecords')
            header.nextButton.addAction(controller: 'crudCompany', modal: true)
            header.nextButton.addAction(controller: 'crudPOGO', modal: true)

            table.with {
                stickyHeaderOffset = 0
//                stickyHeaderZIndex = 9999

                filters.with {
                    fold = false
                    autoFold = true
                    addField(
                            class: Select,
                            id: 'company',
                            optionsFromRecordset: TCompany.list(),
                            transformer: 'TRANSFORM_ME',
//                            renderMessagePrefix: true,
                            multiple: true,
                            placeholder: 'Seleziona qualcosa',
                            cols: 6,
                    )
                    addField(
                            class: TextField,
                            id: 'name',
                            cols: 6,
                    )
                }

                actionbar.with {
                    addAction(
                            action: 'someAction',
                            params: [p1: 1, p2: 2],
                    )
                    addAction(
                            controller: 'someController',
                            params: [p1: 1, p2: 2],
                    )
                }

                groupActions.addAction(
                        action: 'onGroupAction1',
                        submit: ['table'],
                        params: [
                                s: 'Stringa',
                                n: 12345,
                                l: [1, 2, 3], // TODO: Le liste non vengono passate come params
                                m: [firstname: 'Giangio', lastname: 'Sartori'],
                                ml: [ids: [1, 2, 3]],
                                lm: [[id:1], [id:2], [id: 3]] // TODO: Le liste non vengono passate come params
                        ],
                        //confirmMessage: 'Vado?' // TODO: aggiungendo confirmMessage '_tableSelecttion' non viene passata
                )
                groupActions.addAction(action: 'onGroupAction2')
                groupActions.addAction(controller: 'onGroupAction3', confirmMessage: 'Messaggio di conferma, sei sicuro?')

                actions.addAction(action: 'action1.with.a.very.very.long.name', icon: 'fa-arrow-alt-circle-up')
                actions.addAction(action: 'action2', icon: 'fa-arrow-alt-circle-right')
                actions.addAction(controller: 'controller3', icon: 'fa-arrow-alt-circle-down')

                actions.tailAction.order = 9999
                actions.tailAction.text = TextDefault.DELETE
                actions.unsetTailAction()

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

                max = 10
//                sort = [name: 'desc']

                body.eachRow { TableRow row, Map values ->
                    row.verticalAlign = VerticalAlign.TOP

                    row.cells['name'].textWrap = TextWrap.SOFT_WRAP
                    row.cells['company'].textWrap = TextWrap.LINE_WRAP
                    row.cells['company'].icon = 'fa-building'

                    values.employeeCount = values.company.employees?.size()

                    if (values.salary) println prettyPrint(values.salary)
                    if (values.picture) row.cells.picture.icon = 'fa-file'
                    if (values.salary) values.salaryPerMonth = values.salary / 12
                    if (row.actions.hasActions()) row.actions.addSeparator()
                    row.actions.addAction(
                            controller: 'myController',
                            action: 'rowAction3',
                            params: [x: 1, y: 2],
                            icon: 'fa-times-circle',
                    )
                    if (values.name == 'aaa') row.actions.setDefaultAction(controller: 'myController', action: 'rowAction3')
                    //row.tailAction.danger = true

                    row.cells.address.component.textWrap = TextWrap.NO_WRAP
                }
            }
        }

        // QUERY
        //
        def query = TPerson.where {}

        def filters = c.table.filterParams
        if (filters.name) query = query.where { name =~ "%${filters.name}%" }
        if (filters.company) query = query.where { company.id == filters.company }
//        if (filters.company) query = query.where { company.id in filters.company.collect { it.toLong() } }

        // VALUES
        //
        c.table.body = query.list(params)
        c.table.paginate = query.count()

        display content: c
    }

    def onKeyPress() {
        println requestParams
        display
    }

    def onCreateRecords() {
        TCompany dueuno = TCompany.get(1)

        (1..100).each {
            new TPerson(
                    active: true,
                    company: dueuno,
                    name: 'user' + it,
                    address: 'Via del\'automazione, ' + it,
                    postcode: 12345
            ).save(flush: true, failOnError: true)
        }

        display action: 'index'
    }

    def onGroupAction1() {
        println requestParams
        display action: 'index'
    }

    private buildForm(TPerson obj = null) {
        def c = obj
                ? createContent(ContentEdit)
                : createContent(ContentCreate)

        if (requestParams.embedded) {
            c.header.addBackButton(animate: requestParams.animate)
            c.header.backButton.with {
                addAction(action: 'pippo')
            }
            if (requestParams.animate) {
                c.header.nextButton.animate = requestParams.animate
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
                    availableUnits: quantityService.listAllUnits(false, false, true, false, false, false, false),
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
        def company = TCompany.get(requestParams.company)
        t.setValue('companyName', company?.name)
        t.set('name', 'readonly', company)
        display transition: t
    }

    def printWithNiceLabel() {
        def columns = [
                'company',
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
        def rs = TPerson.list()
        printService.printWithNiceLabel(1, rs, columns, 'NLtest', 1) { record ->
            record.company = record.company.id
        }

        display
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
        TPerson obj = new TPerson(requestParams)
        obj.save(flush: true)

        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        if (requestParams.embedded) {
            display returnPoint(person: obj.id) + [modal: true]
        } else {
            display action: 'index'
        }
    }

    def onEdit() {
        TPerson obj = TPerson.read(requestParams.id)
        obj.properties = params
        obj.save(flush: true)

        if (obj.hasErrors()) {
            display errors: obj
        } else {
            display action: 'index'
        }
    }

    def onDelete(TPerson obj) {
        try {
            obj.delete(flush: true, failOnError: true)
            display action: 'index'
        } catch (e) {
            e.printStackTrace()
            display exception: e
        }
    }

//    def exportExcel() {
//        def obj = TPerson.get(1)
//
//        List columns = [
//                'id',
//                'name',
//                'salary',
//                'distanceKm',
//                'dateStart',
//                'dateEnd',
//                'active',
//                'textfield',
//                'numberfield',
//        ]
//
//        Map exportProperties = [
//                labels: [
//                        textfield  : 'Campo di testo',
//                        numberfield: 'Campo numerico',
//                ]
//        ]
//
//        def os = getDownloadOutputStream('ExcelExport.xlsx')
//
//        ExportUtils.toExcel(os, obj.list(), columns, exportProperties) { row ->
//            // Do something with 'row'
//        }
//    }

//    def printPdf() {
//        def xmlWriter = new StringWriter()
//        def xml = new MarkupBuilder(xmlWriter)
//
//        def results = TPerson.list()
//
//        xml.'ns0:CC_Map_Root'('xmlns:ns0': 'http://Greg_Maxey/CC_Mapping_Part') {
//            'ns0:fullname'(security.userFullname)
//            'ns0:username'(security.username)
//
//            results.each { row ->
//                'ns0:row'() {
//                    'ns0:id'(row.id)
//                    'ns0:textfield'(row.textfield)
//                    'ns0:numberfield'(row.numberfield)
//                    'ns0:moneyfield'(row.moneyfield)
//                    'ns0:quantityfield'(row.quantityfield)
//                    'ns0:datefield'(row.datefield)
//                    'ns0:timefield'(row.timefield)
//                    'ns0:checkbox'(row.checkbox)
//                }
//            }
//
//        }

//        printService.downloadPdf(
//            xmlWriter: xmlWriter,
//            response: response,
//            templatePathname: 'printTest.docx',
//            outputFilename: 'Pdf-' + new Date().format('yyyy-MM-dd'),
//        )
//    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def register() {
        def c = createContent(ContentForm)
        c.header.removeNextButton()

        display content: c, modal: true
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def passwordRecovery() {
        def c = createContent(ContentForm)
        c.header.removeNextButton()

        display content: c, modal: true
    }


}
