//= require TextField

class NumberField extends TextField {

    static finalize($element, $root) {
        TextField.finalize($element, $root);

        $element.off('input').on('input', NumberField.onChange);
    }

    static onChange(event) {
        let $element = $(event.currentTarget);
        let properties = Component.getProperties($element);
        let value = $element.val();
        let transformedValue = value;

        let numberValue = parseFloat(value == '-' ? '-0' : value);
        //TODO min value
        //if (properties.min && numberValue < properties.min) {
        //    transformedValue = (properties.min).toString();
        //}
        if (properties.max && numberValue > properties.max) {
            transformedValue = (properties.max).toString();
        }

        if (_21_.user.decimalFormat == "ISO_COM") {
            transformedValue = transformedValue.replace(".", ",");
        } else {
            transformedValue = transformedValue.replace(",", ".");
        }

        if (transformedValue != value) {
            let selStart = event.target.selectionStart;
            $element.val(transformedValue);
            event.target.selectionStart = selStart;
            event.target.selectionEnd = selStart;
        }

        Transition.triggerEvent($element, 'change');
    }

    static getValue($element) {
        let value = $element.val();

        if (_21_.user.decimalFormat == "ISO_COM") {
            value = value.replace(',', '.');
        }

        return {
            type: Type.NUMBER,
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
