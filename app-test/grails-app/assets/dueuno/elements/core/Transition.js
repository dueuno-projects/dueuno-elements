class Transition {

    static wsConnect() {
        const wsClient = new StompJs.Client();
        if (isDevelopment()) {
            wsClient.debug = console.log;
        }
        wsClient.brokerURL = 'ws' + _21_.app.url.replace('http', '') + 'stomp';
        wsClient.onConnect = Transition.wsOnConnect;
        wsClient.activate();
    }

    static async wsOnConnect(frame) {
        let wsClient = this;
        let username = frame.headers['user-name'];
        Transition.wsSubscribe(wsClient, "/queue/username/" + username);

        let url = _21_.app.url + "transition/channels";
        let response = await fetch(url);
        if (!response.ok) {
            Log.error(response.status);
            return;
        }

        let channels = await response.json();
        for (let channel of channels) {
            Transition.wsSubscribe(wsClient, "/queue/channel/" + channel);
        }
    }

    static wsOnError(frame) {
        Log.debug(frame);
    }

    static wsSubscribe(wsClient, channel) {
        wsClient.subscribe(channel, Transition.executeFromWebsocket);
        Log.debug('Subscribed to ' + channel);
    }

    static executeFromWebsocket(message) {
        let transition = JSON.parse(message.body);

        Log.debug('WEB SOCKET > TRANSITION >>>');
        Log.debug(transition.commands);
        Log.debug('<<<');
        Log.debug('');

        for (let command of transition.commands) {
            Transition.executeCommand(command, null);
        }
    }

    static execute(commands, componentEvent) {
        Log.debug('TRANSITION >>>');
        Log.debug(commands);
        Log.debug('<<<');
        Log.debug('');

        for (let command of commands) {
            Transition.executeCommand(command, componentEvent);
        }
    }

    static executeCommand(command, componentEvent) {
        let $element = Transition.getTargetElement(command.component);
        let component = $element.exists() ? Elements.getByElement($element) : null;
        let componentId = $element.exists() ? Component.getId($element) : null;
        let method = command.method;
        let property = command.property;
        let valueMap = command.value;
        let trigger = command.trigger;

        Log.trace('EXECUTING: ' + method + ' ' + componentId + '.' + property + ' = ' + JSON.stringify(valueMap));

        switch (method) {
            case TransitionCommand.REDIRECT:
                TransitionCommand.redirect(valueMap.value);
                break;

            case TransitionCommand.CONTENT:
                TransitionCommand.renderContent($(valueMap.value), componentEvent);
                break;

            case TransitionCommand.REPLACE:
                TransitionCommand.replace($element, $(valueMap.value));
                break;

            case TransitionCommand.APPEND:
                TransitionCommand.append($element, $(valueMap.value));
                break;

            case TransitionCommand.CALL:
                TransitionCommand.call($element, component, property, valueMap.value);
                break;

            case TransitionCommand.SET:
                TransitionCommand.set($element, component, property, valueMap, trigger);
                break;

            case TransitionCommand.TRIGGER:
                TransitionCommand.trigger($element, property);
                break;

            default:
                Log.error('Bad transition command "' + command.method + '"');
                Log.error(JSON.stringify(command));
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
                $root = Page.$content;
                if (rootName == target) return $root;
                break;

            default:
                $root = PageModal.isActive ? PageModal.$self : Page.$content;
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
            Log.error('Cannot find component "' + componentName + '"');
            return $(null);

        } else {
            return $component;
        }
    }

    static submitEvent($element, eventName) {
        let componentEvent = Component.getEvent($element, eventName);
        if (componentEvent) {
            Transition.submit(componentEvent);
        }
    }

    static async submit(componentEvent, async = true) {
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
        if (Elements.onMobile && renderProperties) {
            renderProperties['updateUrl'] = false;
        }

        let values = Transition.build21Params(componentEvent);

        if (async) {
            Transition.call(url, values, componentEvent);
        } else {
            await Transition.call(url, values, componentEvent);
        }
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
                    let $root = PageModal.isActive ? PageModal.$self : Page.$content;
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
            }
        }

        _21Params.components = components;
        _21Params.params = params;

        return {_21Params: JSON.stringify(_21Params)};
    }

    static call(url, values, componentEvent) {
        Log.debug('');
        Log.debug('CALL >>>');
        Log.debug('URL: ' + url);
        Log.debug('PARAMS: ' + JSON.stringify(values));
        Log.debug('<<<');

        Transition.fetchCall(url, values, componentEvent);
    }

    static async fetchCall(url, values, componentEvent, callback = null) {
        try {
            let response = await fetch(url, {
                method: 'POST',
                body: new URLSearchParams(values),
            });

            if (response.redirected) {
                window.location.href = response.url;

            } else if (response.ok) {
                Transition.fetchStatus200(response, componentEvent);

            } else {
                Transition.fetchStatusNot200(response, componentEvent);
            }

        } catch (error) {
            Transition.fetchError(error);
        }
    }

    static async fetchStatus200(response, componentEvent) {
        let body = await response.text();
        let isTransition = $(body).data('21-component') == 'Transition';

        if (isTransition) {
            let transition = Transition.fromHtml(body);
            Transition.execute(transition.commands, componentEvent);

        } else {
            let $responseContent = $(body).find('[data-21-component="PageContent"]');

            if ($responseContent.exists()) { // It's a full page
                PageMessageBox.hide();
                PageModal.close();

                let $pageContent = $('[data-21-component="PageContent"]');
                TransitionCommand.replace($pageContent, $responseContent);

            } else if (typeof callback == 'function') {
                callback(body);
            }
        }
    }

    static async fetchStatusNot200(response, componentEvent) {
        let body = await response.text();

        switch (response.status) {
            case 401: // Unauthorized
                window.location.replace(_21_.app.url + 'logout');
                return;

            case 404: // Not Found
            case 500: // Server Error
                let $content = $(body);
                TransitionCommand.renderContent($content, componentEvent);
                return;

            default:
                PageMessageBox.error(null, {infoMessage: 'Server error: ' + response.status});
        }
    }

    static fetchError(error) {
        PageMessageBox.error(null, {infoMessage: 'Connection error: ' + error.message});
    }

    static fromHtml(html, componentEvent = null) {
        let transition = $(html).find('[data-21-transition]').data('21-transition');

        Log.debug('HTML > TRANSITION >>>');
        Log.debug(transition.commands);
        Log.debug('<<<');
        Log.debug('');

        return transition;
    }
}
