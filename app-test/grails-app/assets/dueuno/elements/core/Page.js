//= require Transition

// Temporary solutions until we get support for static fields
// See: https://github.com/google/closure-compiler/issues/2731
let Page_stickyOffset = 0;

class Page {

    static get stickyOffset() { return Page_stickyOffset }
    static set stickyOffset(value) { Page_stickyOffset = value }

    static register(clazz) {
        let clazzName = clazz.name;
        log.trace("REGISTERING PAGE '" + clazzName + "'");
        Elements.pages.set(
            clazzName,
            clazz
        );
    }

    static render() {
        Page.initializePage();
        Page.initializeContent($('body'));
        $(window).on('load', Page.onLoad);
        LoadingScreen.show(false);
    }

    static onLoad(event) {
        Page.finalizeContent($('body'));
        Page.finalizePage();
        Page.triggerContentChange();
    }

    static initializeContent($root, reinitialize = false) {
        PageContent.initialize();
        Page.initializeComponents($root, reinitialize);
        Page.initializeControls($root, reinitialize);
        Page.initializeControlValues($root, reinitialize);
    }

    static finalizeContent($root, reinitialize = false) {
        Page.finalizeControls($root, reinitialize);
        Page.finalizeComponents($root, reinitialize);
        PageContent.finalize();
    }

    static reinitializeContent($root, reinitialize = false) {
        Page.initializeContent($root, reinitialize);
        Page.finalizeContent($root, reinitialize);
        Page.triggerContentChange();
    }

    static initializePage() {
        if (Elements.onMobile) {
            $(':root').css('--elements-font-size', '16px');
        }

        PageMessageBox.initialize();
        PageModal.initialize();

        let page = Page.get();
        try {
            if (Elements.hasMethod(page, 'initialize')) {
                log.trace("INITIALIZING PAGE '" + page.name + "'");
                Elements.callMethod($('body'), page, 'initialize');
            }
        } catch (e) {
            log.error('Error initializing page "' + page.name + '": ' + e);
        }
    }

    static finalizePage() {
        let page = Page.get();
        try {
            if (Elements.hasMethod(page, 'finalize')) {
                log.trace("FINALIZING PAGE '" + page.name + "'");
                Elements.callMethod($('body'), page, 'finalize');
            }
        } catch (e) {
            log.error('Error finalizing page "' + page.name + '": ' + e);
        }

        $('body').css('visibility', 'visible');
    }

    static triggerContentChange() {
        let page = Page.get();
        try {
            if (Elements.hasMethod(page, 'onContentChange')) {
                log.trace("FINALIZING CONTENT '" + page.name + "'");
                Elements.callMethod($('body'), page, 'onContentChange');
            }
        } catch (e) {
            log.error('Error finalizing content for "' + page.name + '": ' + e);
        }
    }

    static initializeComponents($root, reinitialize) {
        let elements = $root.find('[data-21-component]');
        Page.stickyOffset = 0;

        for (let element of elements) {
            let $element = $(element);
            let component = Component.getByElement($element);
            let properties = Component.getProperties($element);

            if (!PageModal.isActive && properties['sticky']) {
                Page.stickyOffset += element.offsetHeight;
                PageContent.$self.css('margin-top', 'calc(' + Page.stickyOffset + 'px + .5rem)');
                Elements.callMethod($element, component, 'setSticky', true);
            }

            let initialized = Elements.callMethod($element, component, 'isInitialized');
            if (initialized && !reinitialize) {
                continue; // Process next
            }

            try {
                if (Elements.hasMethod(component, 'initialize')) {
                    log.trace("INITIALIZING COMPONENT '" + component.name + "'");
                    Elements.callMethod($element, component, 'initialize', $root);
                }
                Elements.callMethod($element, component, 'setDisplay', properties['display']);
                Elements.callMethod($element, component, 'setVisible', properties['visible']);
                Elements.callMethod($element, component, 'setReadonly', properties['readonly']);
            } catch (e) {
                log.error('Error initializing component "' + component.name + '": ' + e);
            }
        }
    }

    static initializeControls($root, reinitialize) {
        let elements = $root.find('[data-21-control]');
        for (let element of elements) {
            let $element = $(element);
            let control = Control.getByElement($element);
            let properties = Component.getProperties($element);

            let initialized = Elements.callMethod($element, control, 'isInitialized');
            if (initialized && !reinitialize) {
                continue; // Process next
            }

            try {
                if (Elements.hasMethod(control, 'initialize')) {
                    log.trace("INITIALIZING CONTROL '" + control.name + "'");
                    Elements.callMethod($element, control, 'initialize', $root);
                }
                Elements.callMethod($element, control, 'setDisplay', properties['display']);
                Elements.callMethod($element, control, 'setVisible', properties['visible']);
                Elements.callMethod($element, control, 'setReadonly', properties['readonly']);
            } catch (e) {
                log.error('Error initializing control "' + control.name + '": ' + e);
            }
        }
    }

    static initializeControlValues($root, reinitialize) {
        let elements = $root.find('[data-21-control]');
        for (let element of elements) {
            let $element = $(element);
            let control = Control.getByElement($element);
            let properties = Component.getProperties($element);

            let initialized = Elements.callMethod($element, control, 'isInitialized');
            if (initialized && !reinitialize) {
                continue; // Process next
            }

            try {
                let value = Elements.callMethod($element, control, 'getServerValue');
                if (value) {
                    Elements.callMethod($element, control, 'setValue', value, false);
                }
            } catch (e) {
                log.error('Error setting value to control "' + control.name + '": ' + e);
            }
        }
    }

    static finalizeComponents($root, reinitialize) {
        let elements = $root.find('[data-21-component]');
        for (let element of elements) {
            let $element = $(element);
            let component = Component.getByElement($element);

            let initialized = Elements.callMethod($element, component, 'isInitialized');
            if (initialized && !reinitialize) {
                continue; // Process next
            }

            try {
                if (Elements.hasMethod(component, 'finalize')) {
                    log.trace("FINALIZING COMPONENT '" + component.name + "'");
                    Elements.callMethod($element, component, 'finalize', $root);
                }
            } catch (e) {
                log.error('Error finalizing component "' + component.name + '": ' + e);
            }

            Elements.callMethod($element, component, 'setInitialized', true);
        }
    }

    static finalizeControls($root, reinitialize) {
        let elements = $root.find('[data-21-control]');
        for (let element of elements) {
            let $element = $(element);
            let control = Control.getByElement($element);

            let initialized = Elements.callMethod($element, control, 'isInitialized');
            if (initialized && !reinitialize) {
                continue; // Process next
            }

            try {
                if (Elements.hasMethod(control, 'finalize')) {
                    log.trace("FINALIZING CONTROL '" + control.name + "'");
                    Elements.callMethod($element, control, 'finalize', $root);
                }
            } catch (e) {
                log.error('Error finalizing control "' + control.name + '": ' + e);
            }

            Elements.callMethod($element, control, 'setInitialized', true);
        }
    }

    static get() {
        let className = Page.getClassName();
        if (!className) {
            log.error('This is not an Elements page. Please add <body data-21-page="MyPageClass"> ... </body> to make it available.');
            return;
        }

        let page = Elements.pages.get(className);
        if (!page) {
            log.error("No Page registered for class '" + className +
                "'. Register a page class before using it (Eg. Page.register("
                + className + ");");
        }
        return page;
    }

    static getClassName() {
        return $('body').data('21-page');
    }

    static getController() {
        let $content
        if (PageModal.isActive) {
            $content = PageModal.$self;
        } else {
            $content = $('body').find('#page-content');
        }

        let controller = Component.getProperty($content, 'controller');
        return controller;
    }

    static getAction() {
        let $content
        if (PageModal.isActive) {
            $content = PageModal.$self;
        } else {
            $content = $('body').find('#page-content');
        }

        let action = Component.getProperty($content, 'action');
        return action;
    }
}