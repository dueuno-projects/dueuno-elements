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
import dueuno.elements.contents.ContentForm
import dueuno.elements.controls.*
import dueuno.elements.core.ElementsController
import dueuno.elements.types.Money
import dueuno.elements.types.Quantity
import dueuno.elements.types.QuantityUnit
import grails.gorm.multitenancy.CurrentTenant

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@CurrentTenant
class TransitionsController implements ElementsController {

    def index() {
        // PAGE SETUP
        //
        def c = createContent(ContentForm)
        c.header.nextButton.text = 'Reload'
        c.header.nextButton.icon = 'fa-rotate-right'

        c.form.with {
            if (params.transitioned) {
                addField(
                        class: Label,
                        id: 'transitioned',
                        text: 'Hai confermato, bravo!',
                        textColor: '#cc0000',
                        backgroundColor: "rgba(${primaryBackgroundColorInt.join(', ')}, ${primaryBackgroundColorAlpha})",
                        border: true,
                        displayLabel: false,
                        cols: 12,
                )
            }

            addField(
                    class: Button,
                    id: 'messageButton',
                    action: 'showMessage',
                    displayLabel: false,
                    cols: 4,
            )
            addField(
                    class: Button,
                    id: 'errorButton',
                    action: 'showError',
                    displayLabel: false,
                    cols: 4,
            )
            addField(
                    class: Button,
                    id: 'confirmButton',
                    action: 'showConfirm',
                    displayLabel: false,
                    cols: 4,
            )
            addField(
                    class: Button,
                    id: 'btn1',
                    display: true,
                    cols: 6,
            )
            addField(
                    class: Button,
                    id: 'btn2',
                    icon: 'fa-user',
                    display: false,
                    cols: 6,
            )
            addField(
                    class: Button,
                    id: 'btn3',
                    visible: true,
                    cols: 6,
            )
            addField(
                    class: Button,
                    id: 'btn4',
                    visible: false,
                    cols: 6,
            )
            addField(
                    class: Select,
                    id: 'user1',
                    optionsFromRecordset: TPerson.list(),
                    keys: ['id'],
                    waitingScreen: true,
                    onChange: 'onSelectChange',
                    submit: 'form',
                    params: [id: 10],
                    cols: 12,
            )
//            .on(event: 'change', submit: 'form', params: [test: [a:1, b:2]], action: 'onSelectChange')

            addField(
                    class: Select,
                    id: 'select2',
                    readonly: true,
//                    onChange: 'onSelect2Change',
                    cols: 12,
            ).on(event: 'change', submit: 'form', action: 'onSelect2Change')

            addField(
                    class: Select,
                    id: 'select3',
                    cols: 12,
            )
            addField(
                    class: MoneyField,
                    id: 'moneyfield',
                    decimals: 2,
                    cols: 12,
            )
            addField(
                    class: TextField,
                    id: 'textfield',
                    cols: 12,
            )
            addField(
                    class: NumberField,
                    id: 'numberfield',
                    decimals: 5,
                    cols: 12,
            )
            addField(
                    class: QuantityField,
                    id: 'quantityfield',
                    unit: QuantityUnit.KM,
                    cols: 12,
            )
            addField(
                    class: DateTimeField,
                    id: 'datetimefield',
                    value: LocalDateTime.now(),
                    cols: 12,
            )
            addField(
                    class: DateField,
                    id: 'datefield',
                    value: LocalDate.now(),
                    cols: 12,
            )
            addField(
                    class: TimeField,
                    id: 'timefield',
                    value: LocalTime.now(),
                    cols: 12,
            )

            addField(
                    class: Textarea,
                    id: 'textarea',
                    maxSize: 160,
                    cols: 12,
            )
            addField(
                    class: Checkbox,
                    id: 'checkbox',
                    cols: 12,
            )
            addField(
                    class: MultipleCheckbox,
                    id: 'multiple',
                    optionsFromRecordset: TPerson.list(),
                    keys: ['id'],
                    cols: 12,
            )
        }

        c.form['select2'].value = 'user3'
        c.form['textfield'].value = 'My text'
        c.form['moneyfield'].value = new Money(33)
        c.form['numberfield'].value = 55.2
        c.form['quantityfield'].value = new Quantity(12, QuantityUnit.KM)
//        c.form['datefield'].value = LocalDate.now()
//        c.form['timefield'].value = LocalTime.now()
        c.form['multiple'].value = ['admin', 'user']

        // RENDERING
        //
        display content: c, modal: true
    }

    def showMessage() {
        def t = createTransition()
        t.infoMessage('transitions.messagebox.ciao', ['Mondo!'])
        display transition: t
    }

    def showError() {
        def t = createTransition()
        t.errorMessage('transitions.messagebox.error', ['Non ha funzionato!'])
        display transition: t
    }

    def showConfirm() {
        Map onConfirm = [
                controller    : 'transitions',
                action        : 'index',
                params: [transitioned: true],
//                url           : 'https://www.google.com',
        ]
        def t = createTransition()
        t.confirmMessage('transitions.messagebox.confirm', ['Bene!'], onConfirm)
        display transition: t
    }

    def onSelectChange() {
        println params

        sleep(1000)
        def t = createTransition()

        t.set('btn1', 'display', false)
        t.set('btn2', 'display', true)
        t.set('btn2', 'icon', 'fa-solid fa-gear')
        t.set('btn2Field', 'display', true)
        t.set('btn3', 'visible', false)
        t.set('btn4', 'visible', true)
        t.set('btn4Field', 'visible', true)
        t.set('select2', 'options',
                Select.optionsFromRecordset(
                        recordset: TPerson.list(),
                        keys: ['id'],
                ))
        t.set('modal.select2', 'readonly', false)
        t.set('modal.select2', 3, true)

        //setting value and currency
        t.set('moneyfield', new Money(44, 'USD'))
        t.set('modal.moneyfield', 'readonly', true)

        //setting value and decimals
        t.set('numberfield', 123.45)
        t.set('numberfield', 'decimals', 3)

        //setting value and unit
        t.set('quantityfield', new Quantity(500, QuantityUnit.M))

        t.set('textfield', 'Test 12345')

        t.set('checkbox', true)
        t.set('checkbox', 'readonly', true)

        t.set('datetimefield', LocalDateTime.now().plusDays(3))
        t.set('datefield', LocalDate.now())
        t.set('datefield', 'readonly', true)
        t.set('timefield', LocalTime.now())
        t.set('timefield', 'readonly', true)

        t.set('multiple', 'options', ['user2', 'user3'])
        t.set('multiple', 'readonly', ['admin'])

        t.set('searchfield', 'admin')

        t.set('moneyfieldField', 'label', 'New label')
        t.set('moneyfieldField', 'nullable', false)

        t.call('page', 'hideWaitingScreen')

        t.set('textfieldField', 'error', 'This field has an error')
        //c.set('textfieldWrapper', 'error', false)

        t.set('textarea', 'readonly', true)

        display transition: t
    }

    def onSelect2Change() {
        def t = createTransition()
        t.set('select3', 'options',
                Select.optionsFromRecordset(
                        recordset: TPerson.list(),
                        keys: ['name'],
                ))
        t.set('select3', 'admin')
        //c.set('select3', 'readonly', false)


        // Nuovi metodi da implementare
        //
//        t.add(control: 'name', before: 'name')
//        t.add(component: 'name', after: 'name')

//        t.remove(control: 'name')
//        t.remove(component: 'name')

        display transition: t
    }

    def onSearchfieldSearch() {
        def t = createTransition()
        t.set('searchfield', 'options',
                Select.optionsFromRecordset(
                        recordset: TPerson.findAllByNameLike("%${params.searchfield}%"),
                        keys: ['id'],
                )
        )

        display transition: t
    }

    def onConfirm() {
        println params
        display action: 'index'
    }
}
