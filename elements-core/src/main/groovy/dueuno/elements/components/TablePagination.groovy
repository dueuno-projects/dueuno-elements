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

import dueuno.elements.core.Component
import dueuno.elements.exceptions.ArgsException
import dueuno.elements.style.TextDefault
import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class TablePagination extends Component {

    Table table
    Link goFirst
    Link goPrev
    Link goNext
    Link goMax20
    Link goMax50

    Number max
    Number offset
    Number total

    TablePagination(Map args) {
        super(args)

        table = (Table) ArgsException.requireArgument(args, 'table')
        total = args.total as Integer

        goFirst = createControl(
                class: Link,
                id: 'goFirst',
                controller: controllerName,
                action: actionName,
                submit: [table.filters.id],
                params: [
                        _21Table: table.id,
                        _21TableOffset: 0,
                ],
                icon: 'fa-angles-left',
                text: '',
                scroll: table.id,
        )
        goPrev = createControl(
                class: Link,
                id: 'goPrev',
                controller: controllerName,
                action: actionName,
                submit: [table.filters.id],
                icon: 'fa-angle-left',
                text: '',
                scroll: table.id,
        )
        goNext = createControl(
                class: Link,
                id: 'goNext',
                controller: controllerName,
                action: actionName,
                submit: [table.filters.id],
                icon: 'fa-angle-right',
                text: TextDefault.NEXT,
                scroll: table.id,
        )
        goMax20 = createControl(
                class: Link,
                id: 'goMax20',
                controller: controllerName,
                action: actionName,
                submit: [table.filters.id],
                params: [
                        _21Table: table.id,
                        _21TableOffset: 0,
                        _21TableMax: 20,
                ],
                text: 'component.table.pagination.display',
                textArgs: [20],
                scroll: table.id,
        )
        goMax50 = createControl(
                class: Link,
                id: 'goMax50',
                controller: controllerName,
                action: actionName,
                submit: [table.filters.id],
                params: [
                        _21Table: table.id,
                        _21TableOffset: 0,
                        _21TableMax: 50,
                ],
                text: 'component.table.pagination.display',
                textArgs: [50],
                scroll: table.id,
        )

        initializeDefaultParams()
    }

    private String getRequestedOffsetName() {
        return table.id + 'RequestedOffset'
    }

    private String getRequestedMaxName() {
        return table.id + 'RequestedMax'
    }

    private void initializeDefaultParams() {
        if (actionSession[requestedOffsetName] != null) {
            setOffset(actionSession[requestedOffsetName] as Integer)
        } else {
            setOffset(0)
        }

        if (actionSession[requestedMaxName] != null) {
            setMax(actionSession[requestedMaxName] as Integer)
        } else {
            setMax(20)
        }
    }

    void setOffset(Integer value) {
        Integer currentOffset = actionSession[requestedOffsetName] as Integer ?: value
        Integer requestedOffset = requestParams._21TableOffset != null
                ? requestParams._21TableOffset as Integer
                : currentOffset
        Boolean hasTableRequest = requestParams._21Table == table.id

        if (hasTableRequest && requestParams._21TableOffset != null) {
            offset = requestedOffset
            actionSession[requestedOffsetName] = requestedOffset
            table.fetchParams.offset = requestedOffset

        } else {
            offset = currentOffset
            table.fetchParams.offset = currentOffset
        }

        requestParams.offset = offset
    }

    void setMax(Integer value) {
        Integer currentMax = actionSession[requestedMaxName] as Integer ?: value ?: null
        Integer requestedMax = requestParams._21TableMax as Integer ?: currentMax
        Boolean hasTableRequested = requestParams._21Table == table.id

        if (hasTableRequested && requestParams._21TableMax) {
            max = requestedMax
            actionSession[requestedMaxName] = requestedMax
            table.fetchParams.max = requestedMax

        } else {
            max = currentMax
            table.fetchParams.max = currentMax
        }

        goPrev.params = [
                _21Table: table.id,
                _21TableOffset: offset - (max ?: 0),
        ]
        goNext.params = [
                _21Table: table.id,
                _21TableOffset: offset + (max ?: 0),
        ]
        requestParams.max = max
    }

    void reset() {
        if (requestParams._21TableOffset != null) {
            return
        }

        offset = 0
        actionSession[requestedOffsetName] = 0
        table.fetchParams.offset = 0
        requestParams._21TableOffset = 0
        goNext.params = [
                _21Table: table.id,
                _21TableOffset: max ?: 0,
        ]
    }

    void setTotal(Number value) {
        total = value
        display = total > 0
    }

    Boolean hasPages() {
        if (max == null) return false
        if (!total) return false
        return total > (max ?: 0)
    }

    Boolean requiresPrev() {
        return offset > 0
    }

    Boolean requiresNext() {
        return offset + (max ?: 0) < total
    }

    String getPrettyTotal() {
        if (!total) return ''
        return prettyPrint(total)
    }

    String getPrettyPagination() {
        if (!total) return ''

        Number offset = offset ?: 0
        Number first = offset + 1
        Number last = (offset + (max ?: 0) < total) ? offset + (max ?: 0) : total
        String of = message('component.table.pagination.of')

        return  first + '-' + last + " ${of} " + prettyPrint(total)
    }
}
