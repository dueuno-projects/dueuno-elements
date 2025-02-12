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
        let $loginError = $('.page-login-error');

        $loginError.addClass('d-none');

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

        LoadingScreen.show(true, 500);

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
                    LoadingScreen.show(false);
                    $loginError.removeClass('d-none');
                    $username.val('').focus();
                    $password.val('');
                }

                if (data.customError) {
                    LoadingScreen.show(false);
                    PageMessageBox.info(null, data.customError);
                    $username.val('').focus();
                    $password.val('');
                }
            })
            .fail(function (request, textStatus, errorThrown) {
                LoadingScreen.show(false);
                PageMessageBox.error(null, {infoMessage: request.status + ': ' + request.responseText});
            });
    }
}

Page.register(Login);