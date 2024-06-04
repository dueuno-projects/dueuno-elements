class HiddenField extends TextField {

    static getValue($element) {
        let value = Control.getServerValue($element);
        value.value = $element.val();
        return value;
    }

}

Control.register(HiddenField);
