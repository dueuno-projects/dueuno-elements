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
package dueuno.elements.controls

import dueuno.elements.components.Button
import dueuno.elements.core.Component
import dueuno.elements.core.Control
import dueuno.elements.core.Elements
import dueuno.elements.core.PrettyPrinter
import dueuno.elements.core.PrettyPrinterProperties
import dueuno.elements.exceptions.ArgsException
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.grails.orm.hibernate.cfg.GrailsHibernateUtil

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class Select extends Control {

    Map options
    Closure forEachOption

    Button actions

    String placeholder
    Boolean allowClear
    Boolean autoSelect // Auto selects if not nullable and the control has only one option
    Boolean multiple
    Boolean search
    Boolean waitingScreen
    Boolean monospace

    Integer searchMinInputLength

    Select(Map args) {
        super(args)

        valueType = 'LIST'

        autoSelect = (args.autoSelect == null) ? true : args.autoSelect
        monospace = (args.monospace == null) ? false : args.monospace
        multiple = (args.multiple == null) ? false : args.multiple
        forEachOption = args.forEachOption as Closure ?: null
        waitingScreen = (args.waitingScreen == null) ? false : args.waitingScreen
        placeholder = args.placeholder ? message(args.placeholder as String) : message('control.select.placeholder')

        searchMinInputLength = (args.searchMinInputLength == null) ? 0 : args.searchMinInputLength as Integer

        if (args.optionsFromRecordset) {
            search = (args.search == null) ? true : args.search
            allowClear = (args.allowClear == null) ? true : args.allowClear
            options = optionsFromRecordset(
                    recordset: args.optionsFromRecordset,
                    keys: args.keys,
                    keysSeparator: args.keysSeparator,
                    prettyPrinter: prettyPrinter,
                    transformer: args.transformer,
                    forEachOption: args.forEachOption,
                    messagePrefix: prettyPrinterProperties.messagePrefix,
                    renderMessagePrefix: args.renderMessagePrefix == null ? false : args.renderMessagePrefix,
                    locale: locale,
            )

        } else if (args.optionsFromList) {
            search = (args.search == null) ? false : args.search
            allowClear = (args.allowClear == null) ? false : args.allowClear
            options = optionsFromList(
                    list: args.optionsFromList,
                    prettyPrinter: prettyPrinter,
                    transformer: args.transformer,
                    forEachOption: args.forEachOption,
                    messagePrefix: prettyPrinterProperties.messagePrefix,
                    renderMessagePrefix: args.renderMessagePrefix == null ? true : args.renderMessagePrefix,
                    locale: locale,
            )

        } else if (args.optionsFromEnum) {
            search = (args.search == null) ? false : args.search
            allowClear = (args.allowClear == null) ? false : args.allowClear
            options = optionsFromEnum(
                    enum: args.optionsFromEnum,
                    prettyPrinter: prettyPrinter,
                    transformer: args.transformer,
                    forEachOption: args.forEachOption,
                    messagePrefix: prettyPrinterProperties.messagePrefix,
                    renderMessagePrefix: args.renderMessagePrefix == null ? true : args.renderMessagePrefix,
                    locale: locale,
            )

        } else {
            search = (args.search == null) ? true : args.search
            allowClear = (args.allowClear == null) ? false : args.allowClear
            options = options(
                    options: args.options,
                    prettyPrinter: prettyPrinter,
                    transformer: args.transformer,
                    forEachOption: args.forEachOption,
                    messagePrefix: prettyPrinterProperties.messagePrefix,
                    renderMessagePrefix: args.renderMessagePrefix == null ? true : args.renderMessagePrefix,
                    locale: locale,
            )
        }

        // Automatically selects the first element if it's the only choice
        if (autoSelect && !nullable && options.size() == 1) {
            defaultValue = options.keySet()[0]
        }

        setMultiple(args.multiple as Boolean)

        for (option in options) {
            option.value = prettyPrint(option.value)
        }

        actions = createControl(
                class: Button,
                id: 'actions',
                group: true,
                dontCreateDefaultAction: true,
                cssClass: 'hide',
        )
    }

    void setMultiple(Boolean value) {
        this.multiple = (value == null) ? false : value
        if (multiple) allowClear = false
    }

    @Override
    Component onSubmit(Map args) {
        args.event = 'change'
        return on(args)
    }

    @Override
    void setValue(Object value) {
        if (Elements.hasId(value)) {
            super.setValue(value['id'], false)
        } else {
            super.setValue(value, false)
        }
    }

    Control addAction(Map args) {
        actions.addAction(args)
        return this
    }

    void removeAction(Map args) {
        actions.removeAction(args)
    }

    //
    // Utils
    //
    static Map optionsFromRecordset(Map args) {
        Collection recordset = args.recordset as Collection ?: []
        List<String> keys = args.keys as List<String>
        String keysSeparator = args.keysSeparator ?: ','
        Closure forEachOption = args.forEachOption as Closure ?: null

        PrettyPrinterProperties prettyPrinterProperties = createItemPrettyPrinterProperties(args, recordset.getAt(0))

        Map results = [:]

        // If the first record is a domain class we auto-setup 'id' as key
        Object firstRecord = recordset ? recordset.getAt(0) : null

        // If the object is a Hibernate Proxy then we get the real Domain Object
        // before to check for an ID
        if (firstRecord && !Elements.isDomainClass(firstRecord.getClass())) {
            firstRecord = GrailsHibernateUtil.unwrapIfProxy(firstRecord)
        }

        if (firstRecord) {
            if (!Elements.hasId(firstRecord) && !keys) {
                throw new ArgsException("Object does not contain an 'id' property. You must specify at least one key in the 'keys' list.")
            } else if (Elements.hasId(firstRecord) && !keys) {
                keys = ['id']
            }
        }

        for (row in recordset) {
            if (forEachOption)
                forEachOption.call(row)

            String description = PrettyPrinter.prettyPrint(row, prettyPrinterProperties)

            results[buildKey(row, keys, keysSeparator)] = description
        }
        return results
    }

    static Map optionsFromList(Map args) {
        List list = args.list as List ?: []
        Closure forEachOption = args.forEachOption as Closure ?: null
        PrettyPrinterProperties prettyPrinterProperties = createItemPrettyPrinterProperties(args, list.getAt(0))

        Map results = [:]
        for (value in list) {
            if (forEachOption)
                forEachOption.call(value)

            String description = PrettyPrinter.prettyPrint(value, prettyPrinterProperties)
            results[(value as String)] = description
        }

        return results
    }

    @CompileDynamic
    static Map optionsFromEnum(Map args) {
        args.list = args.enum?.values()*.name()
        return optionsFromList(args)
    }

    static Map options(Map args) {
        Map options = args.options as Map ?: [:]
        Closure forEachOption = args.forEachOption as Closure ?: null
        PrettyPrinterProperties prettyPrinterProperties = createItemPrettyPrinterProperties(args, options.keySet().getAt(0))

        Map results = [:]
        for (entry in options) {
            if (forEachOption)
                forEachOption.call(entry)

            String description = PrettyPrinter.prettyPrint(entry, prettyPrinterProperties)
            results[(entry.key as String)] = description
        }

        return results
    }

    private static PrettyPrinterProperties createItemPrettyPrinterProperties(Map args, Object firstItem) {
        PrettyPrinterProperties result = new PrettyPrinterProperties()
        result.messagePrefix = args.messagePrefix
        result.renderMessagePrefix = args.renderMessagePrefix
        result.locale = args.locale as Locale

        // We set the 'transformer' property to PrettyPrint the options
        if (args.transformer) result.transformer = args.transformer

        if (args.prettyPrinter) {
            result.prettyPrinter = args.prettyPrinter
        } else if (firstItem) {
            result.prettyPrinter = firstItem.getClass()
        }

        return result
    }

    private static String buildKey(Object obj, List<String> keys, String separator) {
        List results = []
        for (__key__ in keys) {
            results.add(obj[__key__])
        }
        return results.join(separator)
    }

    @Override
    String getPrettyValue() {
        return value
    }

    @Override
    String getValueAsJSON() {
        List<String> values
        if (value in List) {
            values = value.collect { it != null ? it.toString() : null }

        } else {
            values = [value != null ? value.toString() : null]
        }

        Map valueMap = [
                type: valueType,
                value: values,
        ]

        return Elements.encodeAsJSON(valueMap)
    }

    @Override
    String getPropertiesAsJSON(Map properties = [:]) {
        Map thisProperties = [
                multiple: multiple,
                waitingScreen: waitingScreen,
                searchMinInputLength: searchMinInputLength,
                allowClear: allowClear,
                placeholder: placeholder,
                search: search,
                text: [
                        inputTooShort: message('control.select.inputTooShort'),
                        errorLoading: message('control.select.errorLoading'),
                        noResults: message('control.select.noResults'),
                        searching: message('control.select.searching'),
                ]
        ]
        return super.getPropertiesAsJSON(thisProperties + properties)
    }
}