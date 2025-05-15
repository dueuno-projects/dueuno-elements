class TextField extends Control {

    static finalize($element, $root) {
        $element.off('focus').on('focus', Control.onFocus);
        $element.off('paste').on('paste', Control.onPaste);
        $element.off('keypress').on('keypress', TextField.onKeyPress);
        $element.off('input').on('input', TextField.onChange);

        Transition.triggerEvent($element, 'load');
    }

    static onChange(event) {
        let $element = $(event.currentTarget);
        let properties = Component.getProperties($element);
        let value = $element.val();
        let transformedValue = value;

        if (properties.textTransform) {
            transformedValue = TextField.transform(transformedValue, properties);
        }

        if (transformedValue != value) {
            let selStart = event.target.selectionStart;
            $element.val(transformedValue);
            event.target.selectionStart = selStart;
            event.target.selectionEnd = selStart;
        }

        Transition.triggerEvent($element, 'change');
    }

    static onKeyPress(event) {
        if (event.key == 'Enter') {
            event.preventDefault();
            TextField.onEnter(event);
            return;
        }

        let $element = $(event.currentTarget);
        let properties = Component.getProperties($element);
        let value = Control.getEventValue($element, event);

        if (properties.pattern) {
            let pattern = new RegExp(properties.pattern);
            let isValidValue = value.match(pattern);
            if (!isValidValue) {
                event.preventDefault();
            }
        }

        //Transition.triggerEvent($element, 'keypress');
    }

    static onEnter(event) {
        let $element = $(event.currentTarget);
        Transition.triggerEvent($element, 'enter');
    }

    static setValue($element, valueMap, trigger = true) {
        if (!trigger) $element.off('input');

        let value = valueMap['value'];
        let properties = Component.getProperties($element);
        if (properties.textTransform) {
            value = TextField.transform(value, properties);
        }

        $element.val(value);

        if (!trigger) $element.on('input', TextField.onChange);
    }

    static getValue($element) {
        let value = Control.getServerValue($element);
        value['value'] = $element.val();
        return value;
    }

    static setPlaceholder($element, value) {
        $element[0].placeholder = value;
    }

    static setIcon($element, value) {
        let $icon = $element.parent().find('.input-group-text i')
        $icon.removeClass();

        $icon.addClass(value);
        $icon.addClass('fa-fw');
        if (value.search('fa-solid|fa-regular|fa-light|fa-thin|fa-duotone|fa-brand') == -1) {
            $icon.addClass('fa-solid');
        }
    }

    static transform(value, properties) {
        switch (properties.textTransform) {
            case 'uppercase': return value.toUpperCase();
            case 'lowercase': return value.toLowerCase();
            case 'capitalize': return value.replace(/(^\w|\s\w)/g, m => m.toUpperCase());
        }
    }

    static setReadonly($element, value) {
        Component.setReadonly($element, value);

        let $actions = $element.closest('.input-group').find('a');
        Component.setReadonly($actions, value);
    }

}

Control.register(TextField);
