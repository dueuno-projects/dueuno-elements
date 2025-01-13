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
 */

@CompileStatic
class FormField extends Component {

    Component component

    String label
    List labelArgs
    String helpMessage
    List helpMessageArgs
    Boolean nullable

    Boolean displayLabel
    Boolean multiline

    List acceptedCols
    List acceptedRows
    Integer cols
    Integer colsSmall
    Integer rows

    FormField(Map args) {
        super(args)

        component = args.component as Component

        label = args.label
        labelArgs = args.labelArgs as List ?: []
        helpMessage = args.helpMessage
        helpMessageArgs = args.helpMessageArgs as List
        nullable = (args.nullable == null) ? true : args.nullable

        displayLabel = (args.displayLabel == null) ? true : args.displayLabel
        multiline = (args.multiline == null) ? false : args.multiline

        setAcceptedRows(args.acceptedRows == null ? [] : args.acceptedRows as List)
        setAcceptedCols(args.acceptedCols == null ? [] : args.acceptedCols as List)
        setCols(args.cols == null ? 12 : args.cols as Integer, args.colsSmall == null ? 12 : args.colsSmall as Integer)
        setRows(args.rows == null ? 3 : args.rows as Integer)
    }

    void setAcceptedCols(List accepted) {
        if (accepted) {
            acceptedCols = accepted
        } else {
            if (multiline == true) {
                acceptedCols = [3, 4, 5, 6, 7, 8, 9, 10, 11, 12]
            } else {
                acceptedCols = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]
            }
        }
    }

    void setAcceptedRows(List accepted) {
        acceptedRows = accepted
    }

    void setCols(Integer columns, Integer columnsSmall) {
        if (columns in acceptedCols && columnsSmall in acceptedCols) {
            cols = columns
            colsSmall = columnsSmall
        } else {
            throw new ArgsException("The '${component.getClass().simpleName}' control only accepts one of the following values for 'cols': " + acceptedCols.join(', '))
        }
    }

    void setRows(Integer lines) {
        if (acceptedRows) {
            if (lines in acceptedRows) {
                rows = lines
            } else {
                throw new ArgsException("The '${component.getClass().simpleName}' control only accepts one of the following values for 'rows': " + acceptedRows.join(', '))
            }
        } else {
            rows = lines
        }
    }

    String getCols() {
        String colClasses = ' col-sm-' + cols
        if (colsSmall != 12) colClasses += ' col-' + colsSmall
        return colClasses
    }

    String getRows() {
        if (multiline == false || rows <= 1)
            return ''

        return " height: calc(var(--elements-font-size) * 3 * ${rows});"
    }
}
