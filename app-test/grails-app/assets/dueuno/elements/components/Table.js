class Table extends Component {

    static initialize($element, $root) {
        //no-op
    }

    static finalize($element, $root) {
        // Must be initialized on finalization since ti changes the table DOM
        Table.initializeBootstrapTable($element);
//        Table.initializeFixedScrollbar($element);

        $(window).on('scroll', Table.onWindowScroll);

        let $table = $element.find('.fixed-table-body');
        $table.off('scroll').on('scroll', Table.onScroll);

        let $scrollbar = $element.find('.component-table-scrollbar');
        $scrollbar.off('scroll').on('scroll', Table.onScrollbarScroll);

        let $selectAll = $element.find('.component-table-selection-header input');
        $selectAll.off('click').on('click', Table.onSelectAll);

        let $selectRow = $element.find('.component-table-selection input');
        $selectRow.off('click').on('click', Table.onSelectRow);
    }

    static initializeBootstrapTable($element) {
        let $table = $element.find('.component-table-dataset table.table');
        let $tableHeaders = $table.find('th');
        if (!$tableHeaders.exists()) {
            // Bootstrap Table complains with empty tables
            return;
        }

        let offset;
        let stickyHeaderOffset = Component.getProperty($element, 'stickyHeaderOffset');
        if (stickyHeaderOffset) {
            offset = stickyHeaderOffset;

        } else if (PageModal.isActive) {
            // Not working in modals, see: https://github.com/wenzhixin/bootstrap-table/issues/5545
            offset = 85;

        } else {
            offset = ShellNavbar.getHeight() + Page.stickyOffset;
        }

        let options = {
            classes: '',
            showFooter: true,
            // Not working in modals so we don't activate it
            // See: https://github.com/wenzhixin/bootstrap-table/issues/5545
            stickyHeader: PageModal.isActive ? false : true,
            stickyHeaderOffsetY: offset,
        };

        try {
            $table
                .bootstrapTable('destroy')
                .bootstrapTable(options);

            let $stickyHeader = $element.find('.sticky-header-container');
            let stickyHeaderZIndex = Component.getProperty($element, 'stickyHeaderZIndex');
            if (stickyHeaderZIndex > 0) $stickyHeader.css('z-index', stickyHeaderZIndex);

            Page.finalizeContent($element);

        } catch (e) {
            log.error('Cannot initialize the Table component');
            log.error(e);
        }
    }

    static initializeFixedScrollbar($element) {
        let $body = $element.find('tbody');
        let $scrollbar = $element.find('.component-table-scrollbar');
        let $scrollbarContent = $scrollbar.find('div');

        let content = $('#page-content')[0].getBoundingClientRect();
        $scrollbar.css({position: 'absolute', bottom: 0, left: content.left});
        $scrollbarContent.width($body.width());
    }

    static onWindowScroll(event) {
        let $element = $(event.currentTarget);

    }

    static onScroll(event) {
        let $element = $(event.currentTarget);
        let scrollLeft = $element[0].scrollLeft;
        let $scrollbar = $element.closest('.component-table').find('.component-table-scrollbar');

        $scrollbar.scrollLeft(scrollLeft);
    }

    static onScrollbarScroll(event) {
        let $element = $(event.currentTarget);
        let scrollLeft = $element[0].scrollLeft;
        let $table = $element.closest('.component-table').find('.fixed-table-body');

        $table.scrollLeft(scrollLeft);
    }

    static onSelectAll(event) {
        let $element = $(event.currentTarget);
        let $checkboxes = $element.closest('table').find('.component-table-selection input');
        Checkbox.setValue($checkboxes, {value: $element.is(':checked')});

        Table.displayGroupFeatures($element);
    }

    static onSelectRow(event) {
        let $element = $(event.currentTarget);
        Table.displayGroupFeatures($element);
    }

    static setValueByIndex($element, value) {
        let id = Component.getId($element);
        let cellId = '.' + id + '-' + value.row + '-' + value.column;
        let $cell = $element.find(cellId + ' span');
        $cell.text(value.value);
    }

    static setValueByKey($element, value) {
        let $cell = $element.find('.component-table-row:has(.component-table-values):has([data-21-id="' + value.keyName + '"][value="' + value.keyValue + '"]) .' + value.column + ' span');
        $cell.text(value.value);
    }

    static getValues($element) {
        let $rows = $element.find('tbody tr');
        let rows = [];

        let i = 0;
        $rows.each(function () {
            let $row = $(this);
            let $controls = $row.find('[data-21-control][data-21-value]');

            let values = {}
            $controls.each(function () {
                let $control = $(this);
                let control = Control.getByElement($control);
                let name = Control.getId($control);
                let value = Elements.callMethod($control, control, 'getValue');

                let columnName = Table.processColumnName(name);
                values[columnName] = value;
            });

            rows.push({type: 'MAP', value: values});
            i++;
        });

        return {rows: {type: 'LIST', value: rows}};
    }

    static processColumnName(name) {
        if (name.search('-') < 0) {
            return name
        } else {
            return name.split('-')[2];
        }
    }

    static displayGroupFeatures($element) {
        let $tableSelection = $element.closest('table').find('.component-table-selection');
        let countAll = $tableSelection.find('input').length;
        let countChecked = $tableSelection.find('input:checked').length;

        Table.displayGroupActions($element, countChecked > 0);
        Table.displaySelectAllRows($element, countChecked > 0 && countChecked == countAll);
    }

    static displayGroupActions($element, value) {
        let $groupActions = $element.closest('table').find('.component-table-actions-header [data-21-id="groupActions"]');
        let $actionsLabel = $groupActions.next();

        Component.setDisplay($groupActions, value);
        Component.setDisplay($actionsLabel, !value);
    }

    static displaySelectAllRows($element, value) {
        let $tableSelectAllRows = $element.closest('table').find('.component-table-selection-header input');
        Checkbox.setValue($tableSelectAllRows, {value: value});
    }
}

Component.register(Table);



class TableDataset extends Component {

}

Component.register(TableDataset);
