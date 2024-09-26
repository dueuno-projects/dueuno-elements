class HiddenField extends TextField {

    static getValue($element) {
        let valueMap = Control.getServerValue($element);
        return valueMap;
    }

}

Control.register(HiddenField);
