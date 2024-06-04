//= require NumberField

class MoneyField extends NumberField {

    static setValue($element, valueMap, trigger = true) {
        let value = valueMap['value']
        if (!value) {
            NumberField.setValue($element, valueMap);
            return;
        }

        let amount = value['amount'];
        NumberField.setValue($element, {value: amount});

        if (value['currency']) {
            let $currency = $element.prev();
            $currency.text(value['currency']);
        }
    }

    static getValue($element) {
        let valueMap = Control.getServerValue($element);
        valueMap.value['amount'] = NumberField.getValue($element)['value'];

        if (valueMap.value['amount']) {
            return valueMap;
        } else {
            return null;
        }
    }

}

Control.register(MoneyField);
