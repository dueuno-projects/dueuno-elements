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

import dueuno.elements.controls.HiddenField
import dueuno.elements.core.Component
import dueuno.elements.core.PrettyPrinterProperties
import dueuno.elements.exceptions.ArgsException
import dueuno.elements.style.TextAlign
import dueuno.elements.style.TextStyle
import dueuno.elements.style.TextWrap
import dueuno.elements.style.VerticalAlign
import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class TableCell extends Component {

    Table table
    String column
    TableRow row

    Integer colspan
    Integer rowspan

    TextAlign textAlign
    VerticalAlign verticalAlign

    TableCell(Map args) {
        super(args)

        table = (Table) ArgsException.requireArgument(args, 'table')
        row = (TableRow) ArgsException.requireArgument(args, 'row')
        column = ArgsException.requireArgument(args, 'column')

        colspan = 0
        rowspan = 0
        textAlign = TextAlign.DEFAULT
        verticalAlign = VerticalAlign.DEFAULT

        buildCellComponent(args.component as Component)
    }

    @Override
    String getPropertiesAsJSON(Map properties = [:]) {
        Map thisProperties = [
                column: column,
        ]
        return super.getPropertiesAsJSON(thisProperties + properties)
    }

    private void buildCellComponent(Component component) {
        if (!component) {
            setLabel()

        } else {
            setComponent(component)
        }
    }

    void setLabel() {
        addComponent(
                class: Label,
                id: getId() + '-component',
                replace: true,
                messagePrefix: controllerName,
                textWrap: TextWrap.NO_WRAP,
        )
    }

    void setComponent(Component component) {
        if (!row.isHeader) {
            table.hasComponents = true
            row.viewTemplate = 'TableRowComponent'
        }

        component.id = getId() + '-component'
        addComponent(component)
    }

    void setComponent(Map args) {
        if (!row.isHeader) {
            table.hasComponents = true
            row.viewTemplate = 'TableRowComponent'
        }

        args.id = getId() + '-component'
        args.replace = true
        addComponent(args)
    }

    Component getComponent() {
        return getComponent(getId() + '-component')
    }

    void setSubmitValue(Object value) {
        addComponent(
                class: HiddenField,
                id: getId() + '-value',
                value: value,
                replace: true,
        )
    }

    Component getSubmitValue() {
        return getComponent(getId() + '-value')
    }

    void setPrettyPrinterProperties(Map value) {
        if (component in Label) {
            (component as Label).prettyPrinterProperties.set(value)
        }
    }

    PrettyPrinterProperties getPrettyPrinterProperties() {
        if (component in Label) {
            return (component as Label).prettyPrinterProperties
        } else {
            return new PrettyPrinterProperties()
        }
    }

    void setTextWrap(TextWrap value) {
        if (component in Label) (component as Label).textWrap = value
    }

    void setTextStyle(TextStyle value) {
        if (component in Label) (component as Label).setTextStyle(value)
    }

    void setTextStyle(List<TextStyle> value) {
        if (component in Label) (component as Label).setTextStyle(value)
    }

    void setText(Object value) {
        if (component in Label) (component as Label).text = value
    }

    void setIcon(String value) {
        if (component in Label) (component as Label).icon = value
    }

    void setUrl(String value) {
        if (component in Label) (component as Label).url = value
    }

    void setHtml(String value) {
        if (component in Label) (component as Label).html = value
    }

    Boolean isColumnSpanned() {
        Integer span = 0
        table.columns.find { String col ->
            span--
            if (col == column) return true
            TableCell cell = row.cells[col]
            if (cell.colspan > 1) {
                span = cell.colspan
            }
            return false
        }
        return span > 0
    }

}
