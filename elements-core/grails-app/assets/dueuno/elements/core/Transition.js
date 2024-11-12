// Temporary solutions until we get support for static fields
// See: https://github.com/google/closure-compiler/issues/2731
let Transition_loadingScreenTimeoutId = null;

class Transition {

    static get loadingScreenTimeoutId() { return Transition_loadingScreenTimeoutId }
    static set loadingScreenTimeoutId(value) { Transition_loadingScreenTimeoutId = value }

    static wsConnect() {
        const wsClient = new StompJs.Client();
        if (isDevelopment()) {
            wsClient.debug = console.log;
        }
        wsClient.brokerURL = 'ws' + _21_.app.url.replace('http', '') + 'stomp';
        wsClient.onConnect = Transition.wsOnConnect;
        wsClient.activate();
    }

    static wsOnConnect(frame) {
        let wsClient = this;
        let username = frame.headers['user-name'];
        Transition.wsSubscribe(wsClient, "/queue/username/" + username);

        $.ajax({url: _21_.app.url + "transition/channels"})
        .done(function(channels) {
            for (let channel of channels) {
                Transition.wsSubscribe(wsClient, "/queue/channel/" + channel);
            }
        });
    }

    static wsOnError(frame) {
        log.debug(frame);
    }

    static wsSubscribe(wsClient, channel) {
        wsClient.subscribe(channel, Transition.executeFromWebsocket);
        log.debug('Subscribed to ' + channel);
    }

    static executeFromWebsocket(message) {
        // We cannot send $components in a Web Socket transition since
        // Components can only exists in the context of a web request
        let commands = JSON.parse(message.body);
        let transition = {
            commands: commands,
        };

        log.debug('');
        log.debug('>>> WEB SOCKET');
        Transition.log(transition);

        for (let command of transition.commands) {
            Transition.executeCommand(transition, command, null);
        }
    }

    static execute(transition, componentEvent) {
        log.debug('');
        log.debug('<<< RESPONSE (' + componentEvent.controller + '/' + componentEvent.action + ')');
        Transition.log(transition);

        for (let command of transition.commands) {
            Transition.executeCommand(transition, command, componentEvent);
        }
    }

    static executeCommand(transition, command, componentEvent) {
        let method = command.method;
        let $element = Transition.getTargetElement(command.component);
        let component = $element.exists() ? Elements.getByElement($element) : null;
        let componentId = $element.exists() ? Component.getId($element) : null;
        let property = command.property;
        let valueMap = command.value;
        let trigger = command.trigger;
        let $components = transition.$components;

        log.trace('EXECUTING: ' + method + ' ' + componentId + '.' + property + ' = ' + JSON.stringify(valueMap));

        switch (method) {
            case TransitionCommand.REDIRECT:
                TransitionCommand.redirect(valueMap.value);
                break;

            case TransitionCommand.CONTENT:
                TransitionCommand.renderContent($components, componentEvent);
                break;

            case TransitionCommand.REPLACE:
                TransitionCommand.replace($element, valueMap.value, $components);
                break;

            case TransitionCommand.APPEND:
                TransitionCommand.append($element, valueMap.value, $components);
                break;

            case TransitionCommand.TRIGGER:
                TransitionCommand.trigger($element, property);
                break;

            case TransitionCommand.LOADING:
                TransitionCommand.loading(valueMap.value);
                break;

            case TransitionCommand.CALL:
                TransitionCommand.call($element, component, property, valueMap.value);
                break;

            case TransitionCommand.SET:
                TransitionCommand.set($element, component, property, valueMap, trigger);
                break;

            default:
                log.error('Bad transition command "' + command.method + '"');
                log.error(JSON.stringify(command));
        }
    }

    static getTargetElement(componentName) {
        if (!componentName) {
            return $(null);
        }

        let dotPosition = componentName.indexOf('.');
        let rootName = dotPosition > 0 ? componentName.substring(0, dotPosition) : componentName;
        let target = componentName.slice(dotPosition + 1);

        let $root;
        switch (rootName) {
            case 'page':
                $root = $('body');
                if (rootName == target) return $root;
                break;

            case 'messagebox':
                $root = PageMessageBox.$self;
                if (rootName == target) return $root;
                break;

            case 'modal':
                $root = PageModal.$self;
                if (rootName == target) return $root;
                break;

            case 'content':
                $root = PageContent.$self;
                if (rootName == target) return $root;
                break;

            default:
                $root = PageModal.isActive ? PageModal.$self : PageContent.$self;
        }

        // Check for components with dotted name (Eg. 'company.name')
        let $component = $root.find('[data-21-id="' + componentName + '"]');
        if ($component.exists()) {
            return $component;
        }

        // Select the component from its dotted path
        let path = '';
        let nameList = target.split('.');
        for (let name of nameList) {
            path += '[data-21-id="' + name + '"] ';
        }

        $component = $root.find(path);
        if (!$component.exists()) {
            log.error('Cannot find component "' + componentName + '"');
            return $(null);

        } else if ($component.length > 1) {
            log.error('Multiple components found with the same id "' + target
                + '". Do you have a controller named "' + capitalize(target) + "Controller'? ");
            return $(null);

        } else {
            return $component;
        }
    }

    static triggerEvent($element, eventName) {
        let componentEvent = Component.getEvent($element, eventName);
        if (componentEvent) {
            Transition.submit(componentEvent);
        }
    }

    static submit(componentEvent, async = true) {
        let url = Transition.buildUrl(componentEvent);
        if (!url) {
            return;
        }

        if (componentEvent['direct']) {
            let queryString = Transition.buildQueryString(componentEvent)
            window.location.assign(url + queryString);
            return;
        }

        let renderProperties = componentEvent['renderProperties'];
        if (renderProperties) {
            // We don't update the URL on mobile devices to avoid
            // the display of the browser toolbar
            renderProperties['updateUrl'] = !Elements.onMobile;
        }

        let values = Transition.build21Params(componentEvent);
        Transition.call(url, values, componentEvent, async);
    }

    static buildUrl(componentEvent, values = null) {
        if (!componentEvent)
            return null;

        if (componentEvent.url) {
            return componentEvent.url;

        } else {
            let uri = componentEvent.controller + '/' + componentEvent.action;
            return _21_.app.url + uri;
        }
    }

    static buildQueryString(componentEvent) {
        let values = Transition.build21Params(componentEvent);
        let queryString = '?' + new URLSearchParams(values).toString();
        let params = new URLSearchParams(componentEvent['params']).toString();
        if (params) queryString += '&' + params;

        return queryString
    }

    static build21Params(componentEvent) {
        let _21Params = {};
        let components = {};  // Component controls value
        let params = {};      // Strings, Numbers, Boolean or eg. {type: 'ID', value: 1}

        if (componentEvent) {
            // CONTROL OR COMPONENT CONTROLS VALUES
            if (componentEvent['submit']) {
                let componentNames = componentEvent['submit'];
                for (let componentName of componentNames) {
                    let $root = PageModal.isActive ? PageModal.$self : PageContent.$self;
                    let $component = $root.find('[data-21-id="' + componentName + '"]');
                    let control = Control.getByElement($component)
                    let component = Component.getByElement($component);

                    if (control) {
                        components[componentName] = { [componentName]: control.getValue($component) };
                        components[componentName]._21SubmittedName = componentName;

                    } else if (component) {
                        components[componentName] = component.getValues($component);
                        components[componentName]._21SubmittedName = componentName;
                    }
                }
            }

            // PARAMS
            if (componentEvent['params']) {
                let eventParams = Object.entries(componentEvent['params']);
                for (let [key, value] of eventParams) {
                    params[key] = value;
                }
            }

            // Whether to display the whole page or the transition
            if (!componentEvent['direct']) {
                params['_21Transition'] = true
                params['_21Event'] = componentEvent['event']
            }
        }

        _21Params.components = components;
        _21Params.params = params;

        return {_21Params: JSON.stringify(_21Params)};
    }

    static call(url, values, componentEvent, async) {
        log.debug('');
        log.debug('>>> REQUEST');
        log.debug(url);
        log.debug(JSON.stringify(JSON.parse(values._21Params), null, 2));

        Transition.ajaxCall(url, values, componentEvent, async);
    }

    static ajaxCall(url, values, componentEvent, async, callback = null) {
        let call = {
            type: 'POST',
            url: url,
            data: values,
            async: async,
        }

        Transition.showLoadingScreen(componentEvent['loading']);

        $.ajax(call)
            .done(function (data, textStatus, xhr) {
                let $response = $(data);
                let isTransition = $response.data('21-component') == 'Transition';

                if (isTransition) {
                    let transition = Transition.fromHtml(data);
                    Transition.execute(transition, componentEvent);

                } else { // It's a full page
                    TransitionCommand.renderPage($response, componentEvent);
                    Transition.showLoadingScreen(false);
                }
            })
            .fail(function (xhr, textStatus, errorThrown) {
                Transition.showLoadingScreen(false);

                if (xhr.readyState === 0) {
                    PageMessageBox.error(null, {infoMessage: 'Unable to connect to server.'});
                    return;
                }

                switch (xhr.status) {
                    case 401: // Unauthorized
                        window.location.replace(_21_.app.url + 'logout');
                        return;

                    case 403: // Access denied
                        PageMessageBox.error(null, {infoMessage: JSON.parse(xhr.responseText).error});
                        return;

                    case 404: // Not Found
                    case 500: // Server Error
                        let $content = $(xhr.responseText);
                        TransitionCommand.renderContent($content, componentEvent);
                        return;

                    default:
                        PageMessageBox.error(null, {infoMessage: 'Cannot execute call: ' + xhr.status});
                }
            });
    }

    static showLoadingScreen(show) {
        let $loading = PageModal.isActive
            ? $('#modal-loading-screen')
            : $('#loading-screen');

        if (Transition.loadingScreenTimeoutId && !show) {
            clearTimeout(Transition.loadingScreenTimeoutId);
            Transition.loadingScreenTimeoutId = null;
            $loading.css('display', 'none');

        } else if (!Transition.loadingScreenTimeoutId && show) {
            Transition.loadingScreenTimeoutId = setTimeout(() => {
                $loading.css('display', 'block');
            }, 200);
        }
    }

    static fromHtml(html, componentEvent = null) {
        let $html = $(html);
        if ($html.data('21-component') != 'Transition') {
            log.error("BUG: No transition found!");
            return;
        }

        let commands = $html.find('[data-21-commands]').data('21-commands');
        let $components = $html.find('[data-21-components]');
        let transition = {
            commands: commands,
            $components: $components,
        }

        return transition;
    }

    static log(transition) {
        let commands = JSON.stringify(transition.commands, null, 2)
        log.debug(commands);
        let hasComponents = transition.$components && transition.$components.children().length;
        if (hasComponents) {
            for (let element of transition.$components.children().children()) {
                log.debug(element);
            }
        }
    }
}
