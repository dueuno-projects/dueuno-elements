//= require TextField

class PasswordField extends TextField {

    static finalize($element, $root) {
        TextField.finalize($element, $root);

        $element.parent().find('.show-hide-password').on('click', PasswordField.onShowHidePassword);
    }

    static onShowHidePassword(event) {
        let $element = $(event.currentTarget);
        let $input = $element.parent().find('input');

        event.preventDefault();

        if ($input.attr('type') == 'text') {
            PasswordField.setShowPassword($element, false);

        } else if ($input.attr('type') == 'password') {
            PasswordField.setShowPassword($element, true);
        }
    }

    static setShowPassword($element, value) {
        let $input = $element.parent().find('input');
        let $icon = $element.parent().find('.show-password i');

        if (value) {
            $input.attr('type', 'text');
            $icon.removeClass('fa-eye').addClass('fa-eye-slash');
        } else {
            $input.attr('type', 'password');
            $icon.removeClass('fa-eye-slash').addClass('fa-eye');
        }
    }
}

Control.register(PasswordField);
