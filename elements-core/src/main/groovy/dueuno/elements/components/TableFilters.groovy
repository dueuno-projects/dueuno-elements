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

import dueuno.elements.core.Control
import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class TableFilters extends Form {

    Table table

    TableActionbar actionbar

    Boolean isFiltering
    Boolean fold
    Boolean autoFold
    String prettyValues

    Link searchButton
    Link resetButton

    TableFilters(Map args) {
        super(args)

        table = args.table as Table

        display = false
        fold = (args.fold == null) ? true : args.fold
        autoFold = (args.autoFold == null) ? false : args.autoFold
        isFiltering = false
        prettyValues = ''

        // CONTROLS
        //
        actionbar = createComponent(TableActionbar, [
                table: table,
                filters: this,
        ])

        searchButton = createControl(
                class: Link,
                id: 'searchButton',
                action: actionName,
                submit: [id],
                params: [
                        _21Table: table.id,
                        _21FiltersSearch: true,
                        _21TableOffset: 0,
                ],
                icon: 'fa-magnifying-glass',
                text: '',
        )
        resetButton = createControl(
                class: Link,
                id: 'resetButton',
                action: actionName,
                params: [
                        _21Table: table.id,
                        _21FiltersReset: true,
                        _21TableOffset: 0,
                ],
                icon: 'fa-delete-left',
                text: '',
        )
    }

    @Override
    String getPropertiesAsJSON(Map properties = [:]) {
        Map thisProperties = [
                autoFold: autoFold,
        ]
        return super.getPropertiesAsJSON(thisProperties + properties)
    }

    FormField addField(Map args) {
        args.label = (args.label != null) ? args.label : buildLabel(filtersFieldPrefix + args.id)
        args.allowClear = true // for Select controls
        display = true

        FormField field = super.addField(args)

        if (field.component in Control) {
            Control control = field.component as Control
            control.onSubmit(
                    action: actionName,
                    submit: [id],
                    params: [
                            _21Table: table.id,
                            _21FiltersSearch: true,
                            _21TableOffset: 0,
                    ],
            )
        }

        return field
    }

    private String getFiltersFieldPrefix() {
        return 'filters.'
    }

    private void initializeFilter(Control control) {
        if (requestParams._21FiltersReset) {
            if (control.defaultValue != null) {
                Object filterValue = control.defaultValue
                actionSession[control.id] = filterValue
                control.setValue(filterValue, false)

            } else {
                actionSession.remove(control.id)
            }

            return
        }

        // Gets filters from PARAMS and sets the SESSION
        if (requestParams.containsKey(control.id)) {
            Object filterValue = requestParams[control.id]
            actionSession[control.id] = filterValue
            control.setValue(filterValue, false)

            // Gets filters from SESSION
        } else if (actionSession[control.id]) {
            Object filterValue = actionSession[control.id]
            control.setValue(filterValue, false)

        } else if (control.value != null) {
            // Gets filters from the assigned control value
            // See getValues()

        } else if (control.defaultValue != null) {
            control.setValue(control.defaultValue, false)
            requestParams[control.id] = control.defaultValue
        }
    }

    Boolean isFolded() {
        Object sessionParam = actionSession['_21FiltersFolded_' + getId()]
        if (sessionParam) {
            return sessionParam == 'true'
        } else {
            return fold
        }
    }

    Map getValues() {
        Map results = [:]
        isFiltering = false
        String prettyResults = ''

        for (controlEntry in controls) {
            Control control = controlEntry.value

            initializeFilter(control)

            String controlName = control.id - (filtersFieldPrefix)
            Object controlValue = control.value != null ? control.value : control.defaultValue

            if (controlValue) {
                results[controlName] = controlValue
                isFiltering = true
                prettyResults += (prettyResults == '' ? '' : ', ')
                prettyResults += message(controllerName + '.' + filtersFieldPrefix + controlName) + ': ' + control.prettyValue

            } else {
                results.remove(controlName)
            }
        }

//        // Reset table pagination for new searches
//        if (table != null && requestParams._21FiltersSearch) {
//            table.pagination.reset()
//        }

        prettyValues = prettyResults
        return results
    }
}
