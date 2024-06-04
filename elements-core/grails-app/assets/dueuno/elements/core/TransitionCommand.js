class TransitionCommand {

    // Temporary solutions until we get support for static fields
    // See: https://github.com/google/closure-compiler/issues/2731
    static get REDIRECT() { return 'REDIRECT' }
    static get CONTENT() { return 'CONTENT' }
    static get REPLACE() { return 'REPLACE' }
    static get APPEND() { return 'APPEND' }
    static get CALL() { return 'CALL' }
    static get SET() { return 'SET' }
    static get TRIGGER() { return 'TRIGGER' }

    static async redirect(componentEvent) {
        if (PageModal.isActive && !componentEvent.renderProperties['modal']) {
            PageModal.close();
            await sleep(100); // We give time for the animations to start
        }

        componentEvent['updateUrl'] = true
        Transition.submit(componentEvent, false);
    }

    static renderContent($content, componentEvent) {
        let contentRenderProperties = Component.getProperty($content, 'renderProperties');
        componentEvent.renderProperties = TransitionCommand.mergeRenderProperties(componentEvent.renderProperties, contentRenderProperties);

        if (PageMessageBox.isActive) PageMessageBox.hide();
        if (componentEvent.renderProperties['modal']) {
            PageModal.open($content, componentEvent);

        } else {
            if (PageModal.isActive) PageModal.close();

            let animation = componentEvent.renderProperties['animate'];
            TransitionCommand.animate(animation, Page.$content, $content)

            Page.$content.replaceWith($content);
            Page.reinitializeContent($content);

            TransitionCommand.scrollTo($content, componentEvent);
            if (componentEvent.renderProperties['updateUrl']) {
                let url = Transition.buildUrl(componentEvent);
                TransitionCommand.setBrowserUrl(url);
            }
        }
    }

    static scrollTo($content, componentEvent) {
        let scroll = componentEvent.renderProperties['scroll'];
        if (scroll == 'reset') {
            window.scrollTo({top:0, left:0, behavior: 'instant'});

        } else if (scroll == 'top') {
            window.scrollTo({
                top:0,
                left:0,
                behavior: _21_.user.animations ? 'smooth' : 'instant',
            });

        } else { // Scroll to top of the specified component
            let $element = $content.find('[data-21-id="' + scroll + '"]');
            if ($element.exists()) {
                let position = $element.position();
                window.scrollTo({
                    top: position.top,
                    left: position.left,
                    behavior: _21_.user.animations ? 'smooth' : 'instant',
                });
            }
        }
    }

    static replace($element, $content) {
        $element.replaceWith($content);
        Page.reinitializeContent($content);
    }

    static append($element, $content) {
        $element.parent().append($content);
        Page.reinitializeContent($content);
    }

    static call($element, component, property, value) {
        if (Elements.hasMethod(component, property)) {
            Elements.callMethod($element, component, property, value);
        } else {
            log.error('Method "' + component?.name + '.' + property + '()" does not exist');
        }
    }

    static set($element, component, property, value, trigger) {
        let methodName = 'set' + capitalize(property);
        if (!Elements.hasMethod(component, methodName)) {
            log.error('Method "' + component?.name + '.' + methodName + '()" does not exist');
            return;
        }

        if (methodName == 'setValue') {
            Elements.callMethod($element, component, 'setValue', value, trigger);
            if (trigger) Transition.submitEvent($element, 'change');

        } else {
            Elements.callMethod($element, component, methodName, value.value);
        }
    }

    static trigger($element, property) {
        Transition.submitEvent($element, property);
    }

    static mergeRenderProperties(eventRenderProperties, contentRenderProperties) {
        let merged = {};

        if (contentRenderProperties) {
            for (let [key, value] of Object.entries(contentRenderProperties)) {
                if (value != null) merged[key] = value;
            }
        }

        if (eventRenderProperties) {
            for (let [key, value] of Object.entries(eventRenderProperties)) {
                if (value != null) merged[key] = value;
            }
        }

        if (!merged.hasOwnProperty('closeButton')) {
            merged['closeButton'] = true;
        }

        return merged;
    }

    static setBrowserUrl(url) {
        history.pushState({}, '', url);
    }

    static animate(animation, $prevElement, $nextElement) {
        if (!_21_.user.animations || !animation) {
            return;
        }

        switch (animation) {
            case 'fade':
                $prevElement.addClass('fade-out');
                $nextElement.addClass('fade-in');
            break;

            case 'back':
            break;

            case 'next':
            break;
        }
    }
}