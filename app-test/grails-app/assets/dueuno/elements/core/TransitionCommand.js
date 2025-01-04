class TransitionCommand {

    // Temporary solutions until we get support for static fields
    // See: https://github.com/google/closure-compiler/issues/2731
    static get REDIRECT() { return 'REDIRECT' }
    static get CONTENT() { return 'CONTENT' }
    static get REPLACE() { return 'REPLACE' }
    static get APPEND() { return 'APPEND' }
    static get TRIGGER() { return 'TRIGGER' }
    static get LOADING() { return 'LOADING' }
    static get CALL() { return 'CALL' }
    static get SET() { return 'SET' }

    static async redirect(componentEvent) {
        LoadingScreen.show(false);

        if (PageModal.isActive && !componentEvent.renderProperties['modal']) {
            PageModal.close();
            await sleep(100); // We give time for the animations to start
        }

        Transition.submit(componentEvent, false);
    }

    static renderPage($newPageContent, componentEvent) {
        LoadingScreen.show(false);

        let $newPage = $newPageContent.find('[data-21-component="PageContent"]');
        if (!$newPage.exists()) {
            return;
        }

        PageMessageBox.hide();
        PageModal.close();

        let $page = $('[data-21-component="PageContent"]');
        TransitionCommand.render($page, $newPage, componentEvent);
    }

    static renderContent($components, componentEvent) {
        LoadingScreen.show(false);

        let $content = $components.find('#page-content');
        if (!$content) {
            log.error("Cannot find a 'Content' component in transition.");
            return;
        }

        let contentRenderProperties = Component.getProperty($content, 'renderProperties');
        componentEvent.renderProperties = TransitionCommand.mergeRenderProperties(componentEvent.renderProperties, contentRenderProperties);

        if (PageMessageBox.isActive) PageMessageBox.hide();
        if (componentEvent.renderProperties['modal']) {
            PageModal.open($content, componentEvent);

        } else {
            if (PageModal.isActive) {
                PageModal.close();
            }

            TransitionCommand.render(PageContent.$self, $content, componentEvent);
        }
    }

    static render($component, $newComponent, componentEvent) {
        let animation = componentEvent.renderProperties['animate'];
        TransitionCommand.animate(animation, $component, $newComponent)

        $component.replaceWith($newComponent);
        Page.reinitializeContent($newComponent);

        TransitionCommand.scrollTo($newComponent, componentEvent);
        if (componentEvent.renderProperties['updateUrl']) {
            let url = Transition.buildUrl(componentEvent);
            TransitionCommand.setBrowserUrl(url);
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

    static loading(show) {
        LoadingScreen.show(show);
    }

    static replace($element, componentId, newComponentId, $components) {
        let $component = $components.find('[data-21-id="' + newComponentId + '"]');
        if (!$component) {
            log.error("Cannot find component '" + newComponentId + "' in transition components payload.")
            return;
        }

        if (!$element.exists()) {
            log.error('Cannot replace component "' + componentId + '" (element not found)');
            return;
        }

        $element.replaceWith($component);
        Page.reinitializeContent($component);
    }

    static append($element, componentId, newComponentId, $components) {
        let $component = $components.find('[data-21-id="' + newComponentId + '"]');
        if (!$component) {
            log.error("Cannot find component '" + newComponentId + "' in transition components payload.")
            return;
        }

        if (!$element.exists()) {
            log.error('Cannot append to component "' + componentId + '" (element not found)');
            return;
        }

        let $parent = $element.closest('[data-21-component]').parent();
        $parent.append($component);
        Page.reinitializeContent($component);
    }

    static trigger($element, componentId, eventName) {
        if (!$element.exists()) {
            log.error('Cannot trigger event "' + eventName + '" for component "' + componentId + '" (element not found)');
            return;
        }

        Transition.triggerEvent($element, eventName);
    }

    static async call($element, componentId, component, property, value) {
        await sleep(100); // We give time for the animations to start

        if (Elements.hasMethod(component, property)) {
            Elements.callMethod($element, component, property, value);
        } else {
            log.error('Cannot find method "' + componentId + '.' + property + '()"');
        }
    }

    static set($element, componentId, component, property, value, trigger) {
        let methodName = 'set' + capitalize(property);
        if (!Elements.hasMethod(component, methodName)) {
            log.error('Cannot find method "' + componentId + '.' + methodName + '()"');
            return;
        }

        if (methodName == 'setValue') {
            Elements.callMethod($element, component, 'setValue', value, trigger);
            if (trigger) Transition.triggerEvent($element, 'change');

        } else {
            Elements.callMethod($element, component, methodName, value.value);
        }
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