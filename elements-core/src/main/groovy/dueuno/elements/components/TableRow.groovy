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

import dueuno.commons.utils.ObjectUtils
import dueuno.commons.utils.StringUtils
import dueuno.elements.controls.Checkbox
import dueuno.elements.controls.HiddenField
import dueuno.elements.core.Component
import dueuno.elements.core.Elements
import dueuno.elements.core.Transformer
import dueuno.elements.exceptions.ArgsException
import dueuno.elements.style.TextAlign
import dueuno.elements.style.TextStyle
import dueuno.elements.style.TextWrap
import dueuno.elements.style.VerticalAlign
import dueuno.elements.types.Money
import dueuno.elements.types.Quantity
import groovy.transform.CompileStatic

import java.time.temporal.Temporal

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class TableRow extends Component {

    Table table
    TableRowset rowset
    Map<String, TableCell> cells
    Map<String, HiddenField> submit
    Integer index
    Object values

    Button actions
    Checkbox selected

    Boolean isHeader
    Boolean isFooter
    Boolean hasSelection

    VerticalAlign verticalAlign
    List<TextStyle> textStyle

    TableRow(Map args) {
        super(args)

        table = (Table) ArgsException.requireArgument(args, 'table')
        rowset = (TableRowset) ArgsException.requireArgument(args, 'rowset')
        index = (Integer) ArgsException.requireArgument(args, 'index')

        cells = [:]
        submit = [:]
        values = args.values ?: [:]

        isHeader = (args.isHeader == null) ? false : args.isHeader
        isFooter = (args.isFooter == null) ? false : args.isFooter
        hasSelection = (args.hasSelection == null) ? true : args.hasSelection

        verticalAlign = VerticalAlign.MIDDLE
        setTextStyle(args.textStyle)

        actions = createControl(
                class: Button,
                id: getId() + '-actions',
                dontCreateDefaultAction: true,
        )

        selected = createControl(
                class: Checkbox,
                id: getId() + '-selected',
                simple: true,
                cssClass: 'selectRow',
                checked: (args.checked == null) ? false : args.checked,
        )
    }

    void preProcessRow() {
        selected.readonly = table.readonly
        values = Elements.toMap(values, table.columns, table.includeValues, table.excludeValues)

        createCells()

        processKeys()
        processActions()
        processTransformers()
        processSubmitValues()
        processPrettyPrinters()
    }

    void postProcessRow() {
        if (!isHeader && !isFooter) {
            // Adds key columns to actions params
            Map _21Params = [
                    _21RowId: id,
            ]
            actions.addParams(_21Params + getKeys())
        }

        // Updates submit values
        for (field in submit) {
            String fieldName = field.key
            HiddenField fieldComponent = field.value
            Object fieldValue = values[fieldName]

            if (Elements.hasId(fieldValue)) {
                fieldComponent.value = fieldValue['id'].toString()

            } else {
                fieldComponent.value = fieldValue
            }
        }

        // Updates cell control values & cell stile
        for (cell in cells) {
            String columnName = cell.key
            TableCell columnCell = cell.value

            if (isHeader) {
                Label cellLabel = columnCell.component as Label
                cellLabel.text = values[columnName]

                if (devDisplayHints) {
                    if (columnName in getKeys()) {
                        cellLabel.prettyPrinterProperties.textPrefix = ''
                        cellLabel.html = '<i class="fa-solid fa-key me-1"></i><span>' + columnName + '</span>'

                    } else {
                        String prefix = cellLabel.prettyPrinterProperties.textPrefix
                        String labelValue = cellLabel.text
                        String labelCode = prefix + '.' + columnName

                        if (labelCode != labelValue) {
                            cellLabel.prettyPrinterProperties.renderTextPrefix = false
                            cellLabel.text = labelValue + ' (' + columnName + ')'
                        }
                    }
                }

            } else {
                // The user could have changed the value in the .eachRow closure that's why we set it
                // in the post-processing instead of the pre-processing
                Object finalValue = ObjectUtils.getValue(values, columnName)
                columnCell.text = finalValue == null ? '' : finalValue
                setCellAlignment(columnName, finalValue)
            }
        }
    }

    private void processActions() {
        if (isHeader || isFooter) {
            return
        }

        if (table.rowActions) {
            actions.copyActionsFrom(table.actions)
        }
    }

    private void processTransformers() {
        if (isHeader) {
            return
        }

        for (item in table.transformers) {
            String columnName = item.key
            String transformerName = item.value
            values[columnName] = Transformer.transform(transformerName, values[columnName])
        }
    }

    private void processSubmitValues() {
        if (isHeader) {
            return
        }

        values['_index_'] = index
        submit['_index_'] = createControl(
                class: HiddenField,
                id: '_index_',
                value: index,
        ) as HiddenField

        for (key in getKeys()) {
            String keyName = key.key
            HiddenField hiddenValue = createControl(
                    class: HiddenField,
                    id: keyName,
                    value: key.value,
            )
            submit[keyName] = hiddenValue
        }

        for (columnName in table.submit) {
            HiddenField hiddenValue = createControl(
                    class: HiddenField,
                    id: getId() + '-' + columnName + '-value',
                    value: values[columnName],
            )
            submit[columnName] = hiddenValue
        }
    }

    private void processPrettyPrinters() {
        if (isHeader) {
            return
        }

        for (cell in cells) {
            String columnName = cell.key
            TableCell columnCell = cell.value

            if (columnCell.component !in Label) {
                continue
            }

            Label cellLabel = columnCell.component as Label
            if (table.prettyPrinterProperties[columnName]) {
                cellLabel.prettyPrinterProperties.set(
                        table.prettyPrinterProperties[columnName]
                )
            }
        }
    }

    TableRow getFirst() {
        return rowset.firstRow
    }

    TableRow getLast() {
        return rowset.lastRow
    }

    Boolean isFirst() {
        return rowset.firstRow != null
    }

    Boolean isLast() {
        return rowset.lastRow != null
    }

    void setTextStyle(Object value) {
        switch (value) {
            case TextStyle:
                textStyle = [value as TextStyle]
                break

            case List<TextStyle>:
                textStyle = value as List<TextStyle>
                break

            default:
                textStyle = [TextStyle.NONE]
        }
    }

    String getTextStyle() {
        return textStyle.join(' ')
    }

    //
    // KEYS
    //
    private Map processKeys() {
        Map results = [:]
        List<String> keyColumns = table.keys

        for (keyColumn in keyColumns) {
            Object value

            try {
                value = values[keyColumn]
            } catch (Exception e) {
                // ignore
            }

            if (keyColumn == 'id') {
                results[keyColumn] = value

                // We copy id's value into null keyColumns (user declared keyColumns that don't match any record value)
                // These keyColumns are used when passing an id to another page to avoid "id" conflicts with the next page
                List customKeyColumns = keyColumns.findAll { values[it] == null }
                for (customKeyColumn in customKeyColumns) {
                    values[customKeyColumn] = value
                }

            } else if (value) {
                // GORM Objects handling
                if (Elements.hasId(value)) {
                    // GORM Keys must be strings otherwise GORM sometimes complains (don't really know why)
                    results[keyColumn] = value['id'].toString()

                } else {
                    results[keyColumn] = value
                }
            }
        }

        return results
    }

    private Map getKeys() {
        Map results = [:]
        for (keyColumn in table.keys) {
            results[keyColumn] = values[keyColumn]
        }
        return results
    }

    private String getKeysAsJSON() {
        return Elements.encodeAsJSON(getKeys())
    }


    //
    // CELLS
    //
    private void createCells() {
        cells = [:]
        for (columnName in table.columns) {
            Boolean isSortableHeader = isHeader && columnName in table.sortable
            if (isSortableHeader) {
                addCellHeaderSortable(columnName)

            } else if (isHeader) {
                addCellHeader(columnName)

            } else {
                addCell(columnName)
            }
        }
    }

    private void setCellAlignment(String columnName, Object value) {
        if (value == null) {
            return
        }

        TableCell cell = cells[columnName]
        if (cell.textAlign == TextAlign.DEFAULT) {
            switch (value) {
                case Boolean:
                    cell.textAlign = TextAlign.CENTER
                    break

                case Number:   // BigDecimal, Integer, Float, etc.
                case Date:
                case Temporal: // LocalDate, LocalTime, LocalDateTime, etc.
                case Money:
                case Quantity:
                    if (cell.textAlign == TextAlign.DEFAULT) {
                        cell.textAlign = TextAlign.CENTER
                    }
                    break

                default:
                    cell.textAlign = TextAlign.START
            }
        }

        if (cell.verticalAlign == VerticalAlign.DEFAULT) {
            cell.verticalAlign = verticalAlign
        }

        // Set header alignment
        for (row in table.header.rows) {
            row.cells[columnName].textAlign = cell.textAlign
        }
    }

    private TableCell addCell(String columnName, Component component = null) {
        String cellName = getId() + '-' + columnName
        TableCell cell = createComponent(
                class: TableCell,
                id: cellName,
                table: table,
                column: columnName,
                row: this,
                component: component,
        )

        cells.put(columnName, cell)
        return cell
    }

    private TableCell addCellHeader(String columnName) {
        Label header = createComponent(
                class: Label,
                id: columnName,
                action: actionName,
                textPrefix: controllerName,
                renderTextPrefix: isHeader && !table.labels[columnName],
                textWrap: TextWrap.NO_WRAP,
                textStyle: TextStyle.BOLD,
                tag: false,
        )

        return addCell(columnName, header)
    }

    private TableCell addCellHeaderSortable(String columnName) {
        String order = table.sort[columnName] == 'asc' ? 'desc' : 'asc'
        Link sortableHeader = createComponent(
                class: Link,
                id: columnName,
                action: actionName,
                params: table.submitParams + (Map) [
                        _21Table    : table.id,
                        _21TableSort: [(columnName): order],
                ],
                textPrefix: controllerName,
                renderTextPrefix: isHeader && !table.labels[columnName],
                textWrap: TextWrap.NO_WRAP,
                textStyle: TextStyle.BOLD,
        )

        return addCell(columnName, sortableHeader)
    }

    void removeSelection() {
        hasSelection = false
    }

    void setIsHeader(Boolean value) {
        isHeader = value
        if (isHeader) {
            selected.id = table.id + '-select-all'
        }
    }
}
