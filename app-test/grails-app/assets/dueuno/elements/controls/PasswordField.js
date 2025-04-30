//= require TextField

class PasswordField extends TextField {

    static finalize($element, $root) {
        TextField.finalize($element, $root);

        $element.parent().find('.show-hide-password').on('click', PasswordField.onShowHidePassword);
    }

    static onShowHidePassword(event) {
        event.preventDefault();
        let $element = $(event.currentTarget);
        let $input = $element.parent().find('input');
        let $icon = $element.find('i');
        if ($input.attr('type') == 'text') {
            $input.attr('type', 'password');
            $icon.removeClass('fa-eye').addClass('fa-eye-slash');
        } else if ($input.attr('type') == 'password') {
            $input.attr('type', 'text');
            $icon.removeClass('fa-eye-slash').addClass('fa-eye');
        }
    }
}

Control.register(PasswordField);
