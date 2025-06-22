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
import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class TableRowset extends Component {

    Table table

    Collection recordset
    List<TableRow> rows
    TableRow firstRow
    TableRow lastRow
    Closure eachRowClosure

    Boolean isHeader
    Boolean isFooter

    TableRowset(Map args) {
        super(args)

        table = (Table) ArgsException.requireArgument(args, 'table')

        rows = []
        firstRow = null
        lastRow = null

        isHeader = (args.isHeader == null) ? false : args.isHeader
        isFooter = (args.isFooter == null) ? false : args.isFooter
    }

    void eachRow(Closure c) {
        eachRowClosure = c
    }

    void setRows(Collection collection) {
        recordset = collection
        rows = []

        Integer i = 0
        for (record in recordset) {
            TableRow row = addRow(i, record)

            if (i == 0) firstRow = row
            if (i == recordset.size() - 1) lastRow = row

            row.preProcessRow()

            if (eachRowClosure) {
                if (eachRowClosure.maximumNumberOfParameters == 1) {
                    eachRowClosure.call(row)
                } else {
                    eachRowClosure.call(row, row.values)
                }
            }

            row.postProcessRow()
            i++
        }
    }

    private String buildRowName(Integer i) {
        if (isHeader) {
            return table.getId() + "-h${i}"
        } else if (isFooter) {
            return table.getId() + "-f${i}"
        } else {
            return table.getId() + "-${i}"
        }
    }

    private TableRow addRow(Integer index, Object values) {
        TableRow row = createComponent(TableRow, buildRowName(index), [
                table: table,
                rowset: this,
                index: index,
                isHeader: isHeader,
                isFooter: isFooter,
                values: values,
        ])

        rows.add(row)
        return row
    }

    List getProcessedRows() {
        return rows
    }

    Boolean hasRows() {
        return rows.size() > 0
    }

    Integer getRowsCount() {
        return rows.size()
    }

    TableRow getLastRow() {
        return lastRow
    }

    TableRow getFirstRow() {
        return firstRow
    }
}

