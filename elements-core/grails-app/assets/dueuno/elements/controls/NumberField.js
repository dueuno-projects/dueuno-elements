//= require TextField

class NumberField extends TextField {

    static finalize($element, $root) {
        TextField.finalize($element, $root);

        $element.off('keypress').on('keypress', NumberField.onKeyPress);
    }

    static onKeyPress(event) {
        TextField.onKeyPress(event);

        let $element = $(event.currentTarget);
        let properties = Component.getProperties($element);
        let value = Control.getEventValue($element, event);

        let pattern = new RegExp(properties.pattern);
        let isValidValue = value.match(pattern);
        let separator = value.slice(-1);
        if (isValidValue && _21_.user.decimalFormat == "ISO_COM" && separator == '.') {
            $element.val(value.slice(0, -1) + ',');
            event.preventDefault();
        }

        let numberValue = parseFloat(value == '-' ? '-0' : value);
        if (properties.min && numberValue < properties.min
            || properties.max && numberValue > properties.max) {
            event.preventDefault();
        }

        Transition.triggerEvent($element, 'keypress');
    }

    static getValue($element) {
        let value = $element.val();

        if (_21_.user.decimalFormat == "ISO_COM") {
            value = value.replace(',', '.');
        }

        return {
            type: 'NUMBER',
            value: value,
        };
    }

    static setValue($element, valueMap, trigger = true) {
        let value = valueMap.value;
        if (value != null) {
            valueMap.value = NumberField.renderLocalizedNumber($element, value.toString());
        }

        TextField.setValue($element, valueMap);
    }

    static renderLocalizedNumber($element, number) {
        let properties = Component.getProperties($element);
        let decimals = properties['decimals'] ?? 0;
        let symbols = NumberField.getNumberSymbols();

        if (isNaN(number)) number = 0;
        else if (typeof number != 'number') number = Number(number);

        let numWithLocalizedSeparator = '';
        if (decimals == null) numWithLocalizedSeparator = number.toString();
        else numWithLocalizedSeparator = number.toFixed(decimals);

        let parts = numWithLocalizedSeparator.split('.');
        parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, symbols.thousandsSeparator);

        return parts.join(symbols.decimalSeparator);
    }

    static getNumberSymbols() {
        let decimalSeparator = '.';
        let thousandsSeparator = '';

        switch (_21_.user.decimalFormat) {
            case "ISO_COM":
                decimalSeparator = ',';
                thousandsSeparator = '';
                break;

            case "ISO_DOT":
                decimalSeparator = '.';
                thousandsSeparator = '';
                break;
        }

        return {
            decimalSeparator: decimalSeparator,
            thousandsSeparator: thousandsSeparator
        };
    }

}

Control.register(NumberField);
