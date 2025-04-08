class Login extends Page {

    static finalize() {
        let $loginButton = $('[data-21-id="login"] > a');
        $loginButton.off('click').on('click', Login.onClick);

        $(document).off('keydown.login').on('keydown.login', Login.onKeyPress);
    }

    static onKeyPress(event) {
        let $element = Elements.getElementById('externalId');

        event.stopPropagation();

        let readingSpeed = 50;
        let bufferCleanupTimeout = 500;
        let timer = Component.getProperty($element, 'timer');
        let lastKey = Component.getProperty($element, 'lastKey');

        if (isNaN(timer)) timer = 0;
        let now = Date.now();
        let timeInterval = now - timer;
        Component.setProperty($element, 'timer', now);
        Component.setProperty($element, 'lastKey', event.key);

        let checkMinTime = (readingSpeed == 0 || timeInterval < readingSpeed);
        let checkMaxTime = (bufferCleanupTimeout == 0 || timeInterval > bufferCleanupTimeout);
        let triggerKey = (event.key == 'Enter');

        if (checkMaxTime) {
            $element.val('');
        }

        let printable = KeyPress.isPrintable(event.keyCode);
        let isModifierPressed = KeyPress.isModifierPressed(event, lastKey);

        if (printable) {
            $element.val($element.val() + event.key);
        }

        if (event.target.tagName == 'INPUT') {
            if (printable && !isModifierPressed && checkMinTime) {
                $(event.target).css('color', 'rgb(var(--elements-tertiary-bg))');
            } else {
                $(event.target).css('color', 'rgb(var(--elements-tertiary-text))');
            }
        }

        if (triggerKey) {
            if ($(event.target).data('21-id') == 'authenticate') {
                if ($element.val().length == 0) {
                    return;
                }
                event.preventDefault();
            }

            if (event.target.tagName == 'INPUT' || event.target.tagName == 'A' || checkMinTime) {
                let data = {};
                let $username = $('[data-21-id="username"]');
                let $password = $('[data-21-id="password"]');

                if ($username.val().length > 0 && $password.val().length > 0) {
                    data = {
                       processUrl: 'authentication/authenticate',
                       username: $username.val(),
                       password: $password.val(),
                   };
                } else if ($element.val().length > 0) {
                    data = {
                        processUrl: 'api/auth/external',
                        externalId: $element.val(),
                    };
                }

                if (checkMinTime && event.target.tagName == 'INPUT') {
                    $(event.target).val('');
                }

                Login.onLogin(null, data);
            }
            $element.val('');
        }
    }

    static onClick(event) {
        event.preventDefault();

        let $username = $('[data-21-id="username"]');
        let $password = $('[data-21-id="password"]');

        let data = {
            processUrl: 'authentication/authenticate',
            username: $username.val(),
            password: $password.val(),
        };

        Login.onLogin(null, data);
    }

    static onLogin($element, data) {

        let call = {
            type: 'POST',
            url: _21_.app.url + data.processUrl,
            data: data,
        }

        let $username = $('[data-21-id="username"]');
        let $password = $('[data-21-id="password"]');
        let $loginError = $('.page-login-error');
        $loginError.addClass('d-none');
        LoadingScreen.show(true, 500);

        $.ajax(call)
        .done(function (data, textStatus, request) {
            if (data.success) {
                let params = $.getQueryParameters();
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