class TextField extends Control {

    static finalize($element, $root) {
        $element.off('focus').on('focus', Control.onFocus);
        $element.off('paste').on('paste', Control.onPaste);
        $element.off('input').on('input', TextField.onChange);
        $element.off('keypress').on('keypress', TextField.onKeyPress);

        Transition.submitEvent($element, 'load');
    }

    static onChange(event) {
        let $element = $(event.currentTarget);
        Transition.submitEvent($element, 'change');
    }

    static onKeyPress(event) {
        if (event.key == 'Enter') {
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

        if (properties.textTransform) {
            let transformedValue = TextField.transform(value, properties);
            $element.val(transformedValue);
            event.preventDefault();

            let selStart = event.target.selectionStart;
            event.target.selectionStart = selStart + 1;
            event.target.selectionEnd = selStart + 1;
        }

        Transition.submitEvent($element, 'keypress');
    }

    static onEnter(event) {
        let $element = $(event.currentTarget);
        Transition.submitEvent($element, 'enter');
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

    static transform(value, properties) {
        switch (properties.textTransform) {
            case 'uppercase': return value.toUpperCase();
            case 'lowercase': return value.toLowerCase();
            case 'capitalize': return value.replace(/(^\w|\s\w)/g, m => m.toUpperCase());
        }
    }
}

Control.register(TextField);
