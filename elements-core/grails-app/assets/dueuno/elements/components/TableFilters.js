//= require Form

class TableFilters extends Form {

    static initialize($element, $root) {
        let $table = $element.closest('.component-table');

        let $toggleButton = $element.find('.component-filters-toggle');
        $toggleButton.off('click').on('click', TableFilters.onToggle);

        let tableName = $table.data('21-id');
        let targetRoot = $root.attr('id') ? '#' + $root.attr('id') : 'body';
        let targetBox = targetRoot + ' .component-table[data-21-id="' + tableName + '"] .component-filters-box';
        let targetSearchBtn = targetRoot + ' .component-table[data-21-id="' + tableName + '"] .component-filters-search';
        $toggleButton.attr('data-bs-target', targetBox + ', ' + targetSearchBtn);
    }

    static finalize($element, $root) {
        $element.off('keypress').on('keypress', TableFilters.onSearch);
    }

    static onToggle(event) {
        let $element = $(event.currentTarget);
        let $filters = $element.closest('[data-21-component="TableFilters"]');

        if ($element.hasClass('collapsed')) {
            TableFilters.fold($filters);

        } else {
            TableFilters.unfold($filters);
        }
    }

    static onSearch(event) {
        if (event.key != 'Enter') {
            return;
        }

        event.preventDefault();

        let $element = $(event.currentTarget);
        let autoFold = Component.getProperty($element, 'autoFold');
        if (autoFold) {
            TableFilters.fold($element);
        }

        let name = Component.getId($element);
        Transition.submit({
            controller: Page.getController(),
            action: Page.getAction(),
            submit: [name],
        });
    }

    static unfold($element) {
        let $searchButton = $element.find('.component-filters-search');
        let $filtersBox = $element.find('.component-filters-box');
        $searchButton.addClass('show');
        $filtersBox.addClass('show');

        let name = Component.getId($element);
        Session.setForAction(
            Page.getController(),
            Page.getAction(),
            '_21FiltersFolded_' + name,
            false
        );
    }

    static fold($element) {
        let $searchButton = $element.find('.component-filters-search');
        let $filtersBox = $element.find('.component-filters-box');
        $searchButton.removeClass('show');
        $filtersBox.removeClass('show');

        let name = Component.getId($element);
        Session.setForAction(
            Page.getController(),
            Page.getAction(),
            '_21FiltersFolded_' + name,
            true
        );
    }
}

Component.register(TableFilters);
