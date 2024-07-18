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

import dueuno.elements.components.Button
import dueuno.elements.components.Label
import dueuno.elements.components.Separator
import dueuno.elements.components.Table
import dueuno.elements.contents.ContentForm
import dueuno.elements.controls.*
import dueuno.elements.core.ApplicationService
import dueuno.elements.core.ElementsController
import dueuno.elements.style.TextAlign
import dueuno.elements.style.TextTransform
import dueuno.elements.style.TextWrap
import dueuno.elements.types.QuantityService
import dueuno.elements.types.QuantityUnit
import grails.gorm.multitenancy.CurrentTenant

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@CurrentTenant
class FormController implements ElementsController {

    ApplicationService applicationService
    QuantityService quantityService

    def index() {
        def c = createContent(ContentForm)
        c.header.removeNextButton()
        c.form.with {
            addField(
                    class: Button,
                    id: 'button1',
                    action: 'edit',
                    submit: 'form',
                    stretch: true,
                    cols: 12,
            )
            addField(
                    class: Separator,
                    id: 'separator',
                    text: 'Options',
                    center: true,
                    underline: true,
                    cols: 12,
            )
            addField(
                    class: Checkbox,
                    id: 'modal',
                    displayLabel: false,
                    helpMessage: 'Questo è un messaggio di aiuto in una bottiglia',
                    cols: 6,
            )
            addField(
                    class: Checkbox,
                    id: 'wide',
                    displayLabel: false,
                    cols: 3,
            )
            addField(
                    class: Checkbox,
                    id: 'fullscreen',
                    displayLabel: false,
                    cols: 3,
            )
            addField(
                    class: Checkbox,
                    id: 'grid',
                    displayLabel: false,
                    cols: 6,
            )
            addField(
                    class: Checkbox,
                    id: 'closeButton',
                    displayLabel: false,
                    cols: 6,
            )
            addField(
                    class: Checkbox,
                    id: 'isReadonly',
                    displayLabel: false,
                    cols: 6,
            )
            addField(
                    class: Select,
                    id: 'animate',
                    optionsFromList: ['fade', 'next', 'back'],
                    defaultValue: ['fade'],
                    displayLabel: false,
                    cols: 6,
            )
        }

        display content: c
    }

    def edit() {
        def modal = requireParam('modal')
        def wide = requireParam('wide')
        def fullscreen = requireParam('fullscreen')
        def grid = requireParam('grid')
        def closeButton = requireParam('closeButton')
        def isReadonly = requireParam('isReadonly')
        def animate = requireParam('animate')

        applicationService.registerTransformer('T_PERSON') {
            return "PERSON_TRANS [${it.id}] - ${it.id}"
        }
        applicationService.registerTransformer('CUSTOM_TRANSFORMER') {
            return "CUSTOM_TRANS [${it.key}] - ${it.id} ${it.lastname}"
        }

        applicationService.registerPrettyPrinter('OBJ2TEXT', '${it.name}')

        setReturnPoint()

        println "PERSON: " + params.person

        def c = createContent(ContentForm)
        c.header.with {
            addBackButton(
                    action: 'index',
                    animate: (animate ? 'back' : null)
            )

            nextButton.with {
                addAction(action: 'pluto')
            }
        }

        Integer cols = grid ? 3 : 12
        c.form.with {
            readonly = isReadonly
            def user1 = addField(
                    class: Select,
                    id: 'user1',
                    optionsFromRecordset: TPerson.list(),
                    keys: ['id'],
                    value: params.person,
                    helpMessage: 'Questo è un messaggio di aiuto per te che non sai cosa diavolo fare',
                    cols: cols,
            )/*.on(
                    event: 'create',
                    controller: 'crud',
                    action: 'create',
                    modal: modal,
                    animate: (animate ? 'next' : null),
                    wide: wide,
                    params: [
                            embedded: true,
                    ],
            )*/
            user1.component.addAction(
                    action: 'index',
                    icon: 'fa-truck',
            )
            user1.component.addAction(
                    action: 'index',
                    icon: 'fa-car',
            )
            user1.component.addAction(
                    controller: 'crud',
                    action: 'create',
                    text: '',
                    icon: 'fa-plus',
                    modal: modal,
                    animate: animate,
                    wide: wide,
                    fullscreen: fullscreen,
                    params: [
                            embedded: true,
                            animate : animate,
                    ],
            )
            addField(
                    class: Select,
                    id: 'userTrans',
                    optionsFromRecordset: TPerson.list(),
                    transformer: 'T_PERSON',
                    cols: cols,
            )
            addField(
                    class: Select,
                    id: 'curtomTrans',
                    optionsFromRecordset: [
                            [key: 1, name: 'Gianluca', lastname: 'Sartori'],
                            [key: 2, name: 'Francesco', lastname: 'Piceghello'],
                    ],
                    transformer: 'CUSTOM_TRANSFORMER',
                    search: false,
                    keys: ['key'],
                    value: 2,
                    cols: cols,
            )
            addField(
                    class: TextField,
                    id: 'textfield',
                    textTransform: TextTransform.UPPERCASE,
                    helpMessage: 'Questo è un messaggio di aiuto per te che non sai cosa diavolo fare',
                    cols: cols,
            )
            addField(
                    class: TextField,
                    id: 'textfield2',
                    value: [name: 'pippo'],
                    prettyPrinter: 'OBJ2TEXT',
                    helpMessage: 'Questo è un messaggio di aiuto per te che non sai cosa diavolo fare',
                    cols: cols,
            )
            def textfieldActions = addField(
                    class: TextField,
                    id: 'textfieldActions',
                    prefix: 'PIPPO',
                    cols: cols,
            )
            textfieldActions.component.addAction(
                    action: 'index',
                    submit: 'form',
                    icon: 'fa-truck',
            )
            textfieldActions.component.addAction(
                    action: 'index',
                    submit: 'form',
                    icon: 'fa-car',
            )
            addField(
                    class: NumberField,
                    id: 'numberfield',
                    value: 0,
                    cols: cols,
            ).component.addAction(
                    action: 'increment',
                    submit: 'form',
                    icon: 'fa-plus',
                    text: '',
            ).addAction(
                    action: 'decrement',
                    submit: 'form',
                    icon: 'fa-minus',
                    text: '',
            )
            addField(
                    class: EmailField,
                    id: 'emailfield',
                    placeholder: 'me@mail.com',
                    cols: cols,
            )
            addField(
                    class: UrlField,
                    id: 'urlfield',
                    cols: cols,
            )
            addField(
                    class: PasswordField,
                    id: 'passwordfield',
                    cols: cols,
            )
            addField(
                    class: QuantityField,
                    id: 'quantityfield',
                    defaultUnit: QuantityUnit.KM,
                    availableUnits: quantityService.listAllUnits(),
                    cols: cols,
            )
            addField(
                    class: MoneyField,
                    id: 'moneyfield',
                    cols: cols,
            )
            addField(
                    class: Upload,
                    id: 'upload1',
                    cols: cols,
            )

            addField(
                    class: Upload,
                    id: 'upload2',
                    dragAndDrop: false,
                    cols: cols,
            )
            addField(
                    class: Button,
                    id: 'button',
                    stretch: true,
                    backgroundColor: '#cc0000',
                    textColor: 'white',
                    cols: cols,
            ).component.addAction(action: 'anotherAction1')
                    .addAction(action: 'anotherAction2')
                    .addAction(action: 'anotherAction3')
                    .addAction(action: 'anotherAction4')

            def buttonGroupField = addField(
                    class: Button,
                    id: 'buttonGroup',
                    stretch: true,
                    group: true,
                    backgroundColor: '#cc0000',
                    cols: cols,
            )
            buttonGroupField.component.addAction(action: 'anotherAction1', backgroundColor: '#0000cc')
            buttonGroupField.component.addAction(action: 'anotherAction2', backgroundColor: '#00cc00')
            buttonGroupField.component.addAction(action: 'anotherAction3', backgroundColor: '#cccc00')
            buttonGroupField.component.addAction(action: 'anotherAction4', backgroundColor: '#00cccc')
            addField(
                    class: Separator,
                    id: 'separator',
                    icon: 'fa-car',
                    cols: cols,
            )
            addField(
                    class: Separator,
                    id: 'anotherSeparator',
                    icon: 'fa-truck',
                    cols: cols,
            )
            addField(
                    class: DateField,
                    id: 'datefield',
                    helpMessage: 'Questo è un messaggio di aiuto per te che non sai cosa diavolo fare',
                    cols: cols,
            )
            addField(
                    class: TimeField,
                    id: 'timefield',
                    cols: cols,
            )
            addField(
                    class: DateTimeField,
                    id: 'datetimefield',
                    cols: cols,
            )
            addField(
                    class: Select,
                    id: 'user2',
                    optionsFromRecordset: TPerson.list(),
                    keys: ['id'],
                    search: false,
                    cols: cols,
            )
            addField(
                    class: Checkbox,
                    id: 'checkbox',
                    helpMessage: 'Questo è un messaggio di aiuto per te che non sai cosa diavolo fare',
                    cols: cols,
            )
            addField(
                    class: Checkbox,
                    id: 'checkbox2',
                    value: true,
                    readonly: true,
                    cols: cols,
            )
            addField(
                    class: Checkbox,
                    id: 'simplecheckbox',
                    simple: true,
                    cols: cols,
            )
            /*addField(
                    class: MultipleCheckbox,
                    id: 'multiple',
                    checkboxes: ['1', '2', '3', '4', '5'],
                    messagePrefix: 'checkbox',
                    //simple: true,
            )*/
//            addField(
//                    class: Keypad,
//                    id: 'keypad',
//            )
            addField(
                    class: Textarea,
                    id: 'textarea',
                    maxSize: 100,
                    cols: cols,
                    rows: 5,
            )
            addField(
                    class: Label,
                    id: 'label',
                    cols: cols,
            )
            addField(
                    class: Label,
                    id: 'label2',
                    textAlign: TextAlign.END,
                    cols: cols,
            )
            addField(
                    class: Label,
                    id: 'label3',
                    textWrap: TextWrap.LINE_WRAP,
                    cols: cols,
            )
            addField(
                    class: Label,
                    id: 'info',
                    border: true,
                    displayLabel: false,
                    backgroundColor: '#eab676',
                    cols: cols,
            )
            addField(
                    class: Label,
                    id: 'paragraph',
                    cols: cols,
            )
            //readonly = true
        }

        c.form['datefield'].value = LocalDate.now()
        c.form['timefield'].value = LocalTime.now()
        c.form['datetimefield'].value = LocalDateTime.now()
        c.form['label'].text = """
            [label.text] Lorem ipsum
                    dolor sit amet
        """
        c.form['label2'].text = """[label.text + TextAlign.END]"""
        c.form['label3'].text = """[label.text + TextWrap.LINE_WRAP]
            Lorem ipsum
            dolor sit amet"""
        c.form['info'].text = '[label.text + border] Lorem ipsum dolor sit amet'
        c.form['paragraph'].html = '<em>[label.html]</em> <strong>Lorem ipsum dolor sit amet</strong>, <u>consectetur adipisci elit</u>, <a href="#">sed eiusmod tempor incidunt ut labore et dolore magna aliqua</a>. Ut enim ad minim veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.'

        c.addComponent(Table, 'table').with {
//            rowActions = false
            columns = [
                    'company',
                    'name',
                    'picture',
                    'address',
                    'postcode',
                    'salary',
                    'distanceKm',
                    'dateStart',
                    'dateEnd',
                    'active',
            ]
            actions.addAction(action: 'test1')
            actions.addAction(action: 'test2')
            actions.addAction(action: 'test3')
            actions.addAction(action: 'test4')
            actions.addAction(action: 'test5')
            actions.addAction(action: 'test6')
            def query = TPerson.where {}
            body = query.list(max: 10)
            paginate = query.count()
        }

        display content: c, modal: modal, wide: wide, fullscreen: fullscreen, animate: animate, closeButton: closeButton
    }

    def onConfirm() {
        println params
        display
    }

    def increment() {
        def t = createTransition()
        Integer n = params.numberfield ?: 0
        n++
        t.setValue('numberfield', [value: n])
        display transition: t
    }

    def decrement() {
        def t = createTransition()
        Integer n = params.numberfield ?: 0
        if (n > 0) n--
        t.setValue('numberfield', [value: n])
        display transition: t
    }
}
