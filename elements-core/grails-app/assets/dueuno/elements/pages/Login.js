class Login extends Page {

    static finalize() {
        let $loginButton = $('[data-21-id="login"] > a');
        $loginButton.off('click').on('click', Login.onClick);
        $(document).off('keypress').on('keypress', Login.onKeyPress);
    }

    static onKeyPress(event) {
        if (event.key == 'Enter') {
            Login.onClick(event);
        }
    }

    static async onClick(event) {
        event.preventDefault();

        let $username = $('[data-21-id="username"]');
        let $password = $('[data-21-id="password"]');
        let $rememberMe = $('[data-21-id="rememberMe"]');
        let $loginWheel = $('.page-login-wheel');
        let $loginError = $('.page-login-error');

        $loginError.addClass('d-none');
        $loginWheel.removeClass('d-none');

        let username = $username.val();
        let password = $password.val();
        let rememberMe = $rememberMe.is(':checked');

        try {
            let url = _21_.app.url + 'authentication/authenticate';
            let response = await fetch(url, {
                method: 'POST',
                body: new URLSearchParams({
                    username: username,
                    password: password,
                    'remember-me': rememberMe,
                }),
            });

            if (response.ok) {
                let url = new URL(response.url);
                let loginError = url.searchParams.get('login_error');

                if (loginError) {
                    $loginWheel.addClass('d-none');
                    $loginError.removeClass('d-none');
                    $username.val('').focus();
                    $password.val('');

                } else {
                    window.location.replace(url);
                }

            } else {
                PageMessageBox.error(null, {infoMessage: 'Login error: ' + response.status});
            }

        } catch (error) {
            Transition.fetchError(error);
        }
    }

}

Page.register(Login);