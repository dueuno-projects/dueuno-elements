class Grid extends Component {

    static increment($element, value, $component) {
        let $columns = $element.find('[data-21-component="GridColumn"]');
        let $newColumns = $component.find('[data-21-component="GridColumn"]');
        let elements = [];
        let replace = value.replace == null ? true : value.replace;
        let remove = value.remove == null ? true : value.remove;

        $columns.each(function() {
            let id = $(this).data('21-id');

            elements.push(id);
        });

        $newColumns.each(function() {
            let id = $(this).data('21-id');

            var exists = $element.find('[data-21-id="' + id + '"]');
            if (exists.length) {
                if (replace) {
                    $(exists).replaceWith(this);
                }
                elements = elements.filter(function(value, index, arr){
                    return value != id;
                });
            } else {
                $element.find('.row').first().append(this);
            }
        });

        $.each(elements, function(i, id) {
            if (remove) {
                Grid.removeColumn($element, {id: id});
            }
        });

        Page.reinitializeContent($element);
    }

    static removeColumn($element, value) {
        if (!value.id) {
            log.error('Grid column id not specified');
            return;
        }

        let $column = $element.find('[data-21-component="GridColumn"][data-21-id="' + value.id + '"]');
        let timeout = value.timeout;

        if (timeout) {
            $column.addClass('deleting');
            setTimeout(function() {
                $column.remove();
            }, timeout);
        }

        if (!$column.hasClass('deleting')) {
            $column.remove();
        }
    }

    static addClass($element, value) {
        if (!value.id) {
            log.error('Grid column id not specified');
            return;
        }

        let $column = $element.find('[data-21-component="GridColumn"][data-21-id="' + value.id + '"]');
        let cssClass = value.cssClass;
        let timeout = value.timeout == null ? 0 : value.timeout;

        if (cssClass) {
            setTimeout(function() {
                $column.addClass(cssClass);
            }, timeout);
        }
    }

    static removeClass($element, value) {
        if (!value.id) {
            log.error('Grid column id not specified');
            return;
        }

        let $column = $element.find('[data-21-component="GridColumn"][data-21-id="' + value.id + '"]');
        let cssClass = value.cssClass;
        let timeout = value.timeout == null ? 0 : value.timeout;

        if (cssClass) {
            setTimeout(function() {
                $column.removeClass(cssClass);
            }, timeout);
        }
    }
}

Component.register(Grid);
