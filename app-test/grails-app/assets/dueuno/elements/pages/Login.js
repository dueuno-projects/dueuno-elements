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

    static onClick(event) {
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

        let call = {
            type: 'POST',
            url: _21_.app.url + 'authentication/authenticate',
            data: {
                username: username,
                password: password,
                'remember-me': rememberMe,
            }
        }

        $.ajax(call)
            .done(function (data, textStatus, request) {
                if (data.success) {
                    let params = $.getQueryParameters();
                    // let params = new Map();
                    let url = _21_.app.url.substr(0, _21_.app.url.length - 1);
                    if (params.landingPage) url = url + params.landingPage;
                    else if (data.redirect) url = url + data.redirect;
                    else url = url + '/';
                    window.location.replace(url);
                }

                if (data.error) {
                    $loginWheel.addClass('d-none');
                    $loginError.removeClass('d-none');
                    $username.val('').focus();
                    $password.val('');
                }

                if (data.customError) {
                    $loginWheel.addClass('d-none');
                    PageMessageBox.info(null, data.customError);
                    $username.val('').focus();
                    $password.val('');
                }
            })
            .fail(function (request, textStatus, errorThrown) {
                PageMessageBox.error(null, {infoMessage: 'Error, please retry'});
            });
    }
}

Page.register(Login);