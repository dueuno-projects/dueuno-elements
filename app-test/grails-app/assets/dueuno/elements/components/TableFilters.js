//= require Form

class TableFilters extends Form {

    static initialize($element, $root) {
        let $table = $element.closest('.component-table');

        let $toggleButton = $element.find('.component-filters-toggle');
        $toggleButton.off('click').on('click', TableFilters.onToggleClick);

        let $filtersBox = $element.find('.component-filters-box');
        $filtersBox.off('shown.bs.collapse').on('shown.bs.collapse', TableFilters.onFiltersShowHide);
        $filtersBox.off('hidden.bs.collapse').on('hidden.bs.collapse', TableFilters.onFiltersShowHide);

        let tableName = $table.data('21-id');
        let targetRoot = $root.attr('id') ? '#' + $root.attr('id') : 'body';
        let targetBox = targetRoot + ' .component-table[data-21-id="' + tableName + '"] .component-filters-box';
        let targetSearchBtn = targetRoot + ' .component-table[data-21-id="' + tableName + '"] .component-filters-search';
        $toggleButton.attr('data-bs-target', targetBox + ', ' + targetSearchBtn);
    }

    static finalize($element, $root) {
        // no-op
    }

    static onToggleClick(event) {
        let $element = $(event.currentTarget);
        let $filters = $element.closest('[data-21-component="TableFilters"]');

        if ($element.hasClass('collapsed')) {
            TableFilters.fold($filters);

        } else {
            TableFilters.unfold($filters);
        }
    }

    static onFiltersShowHide(event) {
        let $element = $(event.currentTarget);
        let $table = $element.closest('.component-table');
        let $dataset = $table.find('.component-table-dataset');
        let scrollLeft = $dataset[0].scrollLeft;

        PageContent.updateScrollbar();
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
