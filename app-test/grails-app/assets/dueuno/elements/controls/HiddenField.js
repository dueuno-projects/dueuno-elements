class HiddenField extends Control {

    static setValue($element, valueMap, trigger = false) {
        let value;

        switch (valueMap.type) {
            case 'UNKNOWN':
            case 'BOOLEAN':
            case 'NUMBER':
            case 'TEXT':
                value = valueMap.value;
                break

            default:
                value = JSON.stringify(valueMap.value);
        }

        $element.val(value);
    }

    static getValue($element) {
        let value = Control.getServerValue($element);

        switch (value.type) {
            case 'UNKNOWN':
            case 'BOOLEAN':
            case 'NUMBER':
            case 'TEXT':
                value.value = $element.val();
                break

            default:
                value.value = JSON.parse($element.val());
        }

        return value;
    }

}

Control.register(HiddenField);
