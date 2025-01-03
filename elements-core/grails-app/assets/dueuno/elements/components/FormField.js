class FormField extends Component {

    static setLabel($element, value) {
        let $label = $element.find('label');
        $label.find('span').text(value);
    }

    static setNullable($element, value) {
        let $labelAsterisk = $element.find('label i');
        Component.setDisplay($labelAsterisk, !value);
    }

    static setError($element, value) {
        Transition.showLoadingScreen(false);
        let $errorMessage = $element.find('.error-message');

        if (value) {
            $errorMessage.find('span').html(value);
            Component.setDisplay($errorMessage, true);
            $element.find('.input-group').addClass('error-highlight');

        } else {
            $errorMessage.find('span').html('');
            Component.setDisplay($errorMessage, false);
            $element.find('.input-group').removeClass('error-highlight');
        }
    }

    static setReadonly($element, value) {
        Component.setReadonly($element, value);

        if (value) {
            FormField.setNullable($element, true);

        } else {
            let $control = $element.find('[data-21-control]');
            let nullable = Component.getProperty($control, 'nullable');
            FormField.setNullable($element, nullable);
        }
    }
}

Component.register(FormField);
