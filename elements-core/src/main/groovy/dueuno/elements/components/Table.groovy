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
import dueuno.elements.core.Elements
import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class Table extends Component {

    private List<Map> recordset

    List<String> columns
    List<String> keys
    Map<String, String> sortable
    Map<String, String> sort
    Map<String, Object> fetchParams
    Map<String, Object> submitParams
    List<String> submit
    Map<String, Integer> widths
    Map<String, String> labels
    Map<String, String> transformers
    Map<String, Object> prettyPrinters
    Map<String, Map> prettyPrinterProperties

    List<String> includeValues
    List<String> excludeValues

    Separator title
    TableRowset header
    TableRowset body
    TableRowset footer

    TableActionbar actionbar
    TableFilters filters
    TableDataset dataset
    Button actions
    Button groupActions
    TablePagination pagination

    Boolean stickyHeader
    Double stickyHeaderOffset
    Integer stickyHeaderZIndex

    Boolean hasHeader
    Boolean hasFooter
    Boolean hasPagination
    Boolean hasComponents

    Boolean rowActions
    Boolean rowHighlight
    Boolean rowStriped
    Boolean rowBorderless
    Boolean rowScrollToLastChanged
    Boolean rowHighlightLastChanged

    Boolean noResults
    String noResultsIcon
    String noResultsMessage

    Table(Map args) {
        super(args)

        columns = []
        keys = []
        sortable = [:]
        sort = [:]
        fetchParams = [:]
        submit = []
        submitParams = [:]

        includeValues = []
        excludeValues = []

        widths = [:]
        labels = [:]
        transformers = [:]
        prettyPrinters = [:]
        prettyPrinterProperties = [:]

        recordset = []

        // Number of rows needed to trigger the sticky header
        stickyHeader = args.stickyHeader == null ? true : args.stickyHeader as Boolean
        stickyHeaderOffset = args.stickyHeaderOffset as Double
        stickyHeaderZIndex = args.stickyHeaderZIndex == null ? 0 : args.stickyHeaderZIndex as Integer

        rowActions = (args.rowActions == null) ? true : args.rowActions
        hasHeader = (args.hasHeader == null) ? true : args.hasHeader
        hasFooter = (args.hasFooter == null) ? true : args.hasFooter
        hasPagination = (args.hasPagination == null) ? false : args.hasPagination
        hasComponents = (args.hasComponents == null) ? false : args.hasComponents

        rowHighlight = (args.rowHighlight == null) ? true : args.rowHighlight
        rowStriped = (args.rowStriped == null) ? false : args.rowStriped
        rowBorderless = (args.rowBorderless == null) ? true : args.rowBorderless
        rowScrollToLastChanged = (args.rowScrollToLastChanged == null) ? false : args.rowScrollToLastChanged
        rowHighlightLastChanged = (args.rowHighlightLastChanged == null) ? false : args.rowHighlightLastChanged

        noResults = (args.noResults == null) ? true : args.noResults
        noResultsIcon = (args.noResultsIcon == null) ? 'fa-regular fa-folder-open' : args.noResultsIcon
        noResultsMessage = (args.noResultsMessage) ?: 'default.table.no.results'

        readonly = (args.readonly == null) ? false : args.readonly

        // COMPONENTS
        //
        title = createControl(
                class: Separator,
                id: 'title',
                text: buildLabel(getId()),
                squeeze: true,
                display: false,
        )
        header = createComponent(TableRowset, 'header', [
                table: this,
                isHeader: true,
        ])
        body = createComponent(TableRowset, 'body', [
                table: this,
        ])
        footer = createComponent(TableRowset, 'footer', [
                table: this,
                isFooter: true,
        ])
        filters = createComponent(TableFilters, 'filters', [
                table: this,
        ])
        actionbar = filters.actionbar
        dataset = createComponent(TableDataset, 'dataset', [
                table: this,
        ])
        pagination = createComponent(TablePagination, 'pagination', [
                table: this,
        ])

        // CONTROLS
        //
        actions = createControl(
                class: Button,
                id: 'commonActions',
                dontCreateDefaultAction: true,
        )
        actions.addDefaultAction(
                action: 'edit',
                text: '',
                icon: 'fa-pencil-alt',
        )
        actions.addTailAction(
                action: 'onDelete',
                text: '',
                icon: 'fa-solid fa-trash-alt',
                confirmMessage: 'default.confirm.message',
        )
        groupActions = createControl(
                class: Button,
                id: 'groupActions',
                dontCreateDefaultAction: true,
                display: false,
        )
    }

    static List<Serializable> getSelected(Map params) {
        List<Serializable> ids = params.rows.findAll { it['selected'] }*.getAt('id') as List<Serializable>
        return ids
    }

    @Override
    String getPropertiesAsJSON(Map properties = [:]) {
        Map thisProperties = [
                hasComponents: hasComponents,
                stickyHeaderOffset: stickyHeaderOffset,
                stickyHeaderZIndex: stickyHeaderZIndex,
        ]
        return super.getPropertiesAsJSON(thisProperties + properties)
    }

    private String getRequestedSortName() {
        return id + 'RequestedSort'
    }


    private void initializeTable() {
        filters.display = hasFilters()
    }

    void setSort(Map values) {
        if (!values) {
            return
        }

        Map<String, String> currentSort = actionSession[requestedSortName] as Map<String, String> ?: values
        Map<String, String> requestedSort = requestParams._21TableSort as Map ?: currentSort
        Boolean hasTableRequested = requestParams._21Table == id

        if (hasTableRequested && requestParams._21TableSort) {
            // The required sort column must be the first one on the map
            Map<String, String> head = [:]
            Map<String, String> body = [:]
            for (entry in currentSort) {
                String columnName = entry.key
                String requestedSortColumnName = requestedSort.keySet()[0]
                String requestedSortOrder = requestedSort.values()[0]

                if (columnName == requestedSortColumnName) {
                    head << [(columnName): requestedSortOrder]
                } else {
                    body << [(columnName): entry.value as String]
                }
            }

            sort = head + body
            actionSession[requestedSortName] = sort
            fetchParams.sort = sort as Map<String, Object>

        } else {
            sort = currentSort
            fetchParams.sort = currentSort
        }

        requestParams.sort = sort
    }

    void setOffset(Integer value) {
        pagination.setOffset(value)
    }

    void setMax(Integer value) {
        pagination.setMax(value)
    }

    void setColumnsFromClass(Class clazz) {
        List<String> properties
        if (Elements.isDomainClass(clazz)) {
            properties = (clazz['constrainedProperties'] as Map).collect { Map.Entry it -> it.key as String }
        } else {
            properties = clazz.declaredFields*.name
        }

        properties.remove("class")
        columns = properties.sort()
    }

    void setSortable(Map values) {
        sortable = values
        if (sortable) {
            setSort(sortable)
        }
    }

    void setPaginate(Number total) {
        pagination.total = total
        hasPagination = true
    }

    void setHeader(Collection recordset) {
        header.rows = recordset
    }

    void setBody(Collection recordset) {
        if (!recordset)
            return

        initializeTable()
        initializeKeyColumns(recordset)
        initializeDevColumns()

        // Loading table data
        header.rows = buildHeaders()
        body.rows = recordset
    }

    void setFooter(Collection recordset) {
        if (!recordset)
            return

        if (!body.hasRows())
            return

        // Table initialization
        footer.rows = recordset
    }

    void setSave(Object recordset) {
        actionbar.addAction(controller: 'table', action: 'download', icon: 'fa-download')
    }

    private List buildHeaders() {
        Map displayColumns = [:]

        for (column in columns) {
            if (labels[column] != null) {
                displayColumns[column] = labels[column]
            } else {
                displayColumns[column] = column
            }
        }

        return [displayColumns]
    }

    private void initializeKeyColumns(Collection recordset) {
        // Add 'id' key if the first record contains an id
        if (recordset.size() > 0 && Elements.hasId(recordset[0])) {
            if ('id' !in keys) keys.add('id')

            // First column is the default key
        } else if (columns.size() > 0 && keys.size() == 0) {
            keys = [columns[0]]
        }
    }

    void initializeDevColumns() {
        if (devDisplayHints) {
            List<String> devCols = []
            for (keyColumn in keys) {
                if (keyColumn !in columns) {
                    devCols.add(keyColumn)
                }
            }
            columns = devCols + columns
        }
    }

    Integer getColumnsNumber() {
        Integer result = columns.size()
        if (rowActions) result++
        if (groupActions.hasActions()) result++
        return result
    }

    //
    // FILTERS
    //
    Boolean hasFilters() {
        return filters.controls
    }

    Map getFilterParams() {
        return filters.values
    }
}
