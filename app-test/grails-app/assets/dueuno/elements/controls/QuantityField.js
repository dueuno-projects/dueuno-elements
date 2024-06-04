//= require NumberField

class QuantityField extends NumberField {

    static finalize($element, $root) {
        NumberField.finalize($element, $root);

        let $dropdownItem = $element.closest('.input-group').find('li a');
        if ($dropdownItem.exists()) {
            $dropdownItem.off('click').on('click', QuantityField.onSelectUnit);
        }
     }

     static onSelectUnit(event) {
        let $selectedUnit = $(this);
        let unitKey = $selectedUnit.data('21-unit');
        let $element = $selectedUnit.closest('.input-group').find('.control-quantity-field');

        QuantityField.setUnit($element, unitKey);
     }

    static setValue($element, valueMap, trigger = true) {
        let value = valueMap['value'];
        if (!value) {
            NumberField.setValue($element, valueMap);
            return;
        }

        let amount = value['amount'];
        NumberField.setValue($element, {value: amount});

        if (value['unit']) {
            QuantityField.setUnit($element, value['unit']);
        }
    }

    static getValue($element) {
        let valueMap = Control.getServerValue($element);
        valueMap.value['unit'] = QuantityField.getUnit($element);
        valueMap.value['amount'] = NumberField.getValue($element)['value'];

        if (valueMap.value['amount']) {
            return valueMap;
        } else {
            return null;
        }
    }

    static setUnit($element, value) {
        let $unit = $element.closest('.input-group').find('.control-quantity-field-unit');
        let $unitItem = $element.closest('.input-group').find('li a[data-21-unit="' + value + '"]');
        let unitLabel = $unitItem.html();

        $unit.data('21-unit', value);
        $unit.html(unitLabel);
    }

    static getUnit($element) {
        let $unit = $element.closest('.input-group').find('.control-quantity-field-unit');
        if (!$unit.length) {
            $unit = $element.closest('.input-group').find('.input-group-text');
        }
        let unitKey = $unit.data('21-unit');
        return unitKey;
    }

    static setReadonly($element, value) {
        Control.setReadonly($element, value);

        // If we can select the unit we need to disable the dropdown button as well
        let $button = $element.siblings('button');
        if (!$button.exists()) {
            return;
        }

        $button.prop('disabled', value);
        if (value) {
            // This is here to set just the attribute with no value
            let button = $button[0];
            button.setAttribute('disabled', '');

        } else {
            $button.removeAttr('disabled');
        }
    }
}

Control.register(QuantityField);
