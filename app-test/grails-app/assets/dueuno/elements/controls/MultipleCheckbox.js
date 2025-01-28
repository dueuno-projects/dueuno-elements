class MultipleCheckbox extends Control {

    static getValue($element) {
        let items = [];
        let $checkboxes = $element.siblings().find('input[type="checkbox"]');
        $checkboxes.each(function () {
            if ($(this).is(':checked')) {
                let value = Control.getProperty($(this), 'option');
                items.push(value);
            }
        });
        return items;
    }

    static setValue($element, valueMap, trigger = true) {
        let $checkboxes = $element.siblings().find('input[type="checkbox"]');
        $checkboxes.each(function () {
            let control = Control.getByElement($(this));
            let controlValue = Control.getProperty($(this), 'option');
            if (valueMap['value'].includes(controlValue)) {
                Elements.callMethod($(this), control, 'setValue', true);
            }
        });
    }

}

Control.register(MultipleCheckbox);
