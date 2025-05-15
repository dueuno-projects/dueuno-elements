//= require Transition

class Page {

    static get $self() { return $('body') }

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
        Page.initializeContent(Page.$self);
        $(window).on('load', Page.onLoad);
        LoadingScreen.show(false);
    }

    static onLoad(event) {
        Page.finalizeContent(Page.$self);
        Page.finalizePage();
        Page.triggerContentChange();
    }

    static initializeContent($root, reinitialize = false) {
        PageStickyBox.initialize();
        PageTooltips.initialize();
        Page.initializeComponents($root, reinitialize);
        Page.initializeControls($root, reinitialize);
        Page.initializeControlValues($root, reinitialize);
    }

    static finalizeContent($root, reinitialize = false) {
        PageStickyBox.finalize();
        PageTooltips.finalize();
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
                Elements.callMethod(Page.$self, page, 'initialize');
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
                Elements.callMethod(Page.$self, page, 'finalize');
            }
        } catch (e) {
            log.error('Error finalizing page "' + page.name + '": ' + e);
        }

        Page.$self.css('visibility', 'visible');
    }

    static triggerContentChange() {
        let page = Page.get();
        try {
            if (Elements.hasMethod(page, 'onContentChange')) {
                log.trace("FINALIZING CONTENT '" + page.name + "'");
                Elements.callMethod(Page.$self, page, 'onContentChange');
            }
        } catch (e) {
            log.error('Error finalizing content for "' + page.name + '": ' + e);
        }
    }

    static initializeComponents($root, reinitialize) {
        let $elements = $root.find('[data-21-component]');
        for (let element of $elements) {
            let $element = $(element);
            let component = Component.getByElement($element);
            let properties = Component.getProperties($element);

            let isInitialized = Elements.callMethod($element, component, 'isInitialized');
            if (isInitialized && !reinitialize) {
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
        let $elements = $root.find('[data-21-control]');
        for (let element of $elements) {
            let $element = $(element);
            let control = Control.getByElement($element);
            let properties = Component.getProperties($element);

            let isInitialized = Elements.callMethod($element, control, 'isInitialized');
            if (isInitialized && !reinitialize) {
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
        let $elements = $root.find('[data-21-control]');
        for (let element of $elements) {
            let $element = $(element);
            let control = Control.getByElement($element);
            let properties = Component.getProperties($element);

            let isInitialized = Elements.callMethod($element, control, 'isInitialized');
            if (isInitialized && !reinitialize) {
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
        let $elements = $root.find('[data-21-component]');
        for (let element of $elements) {
            let $element = $(element);
            let component = Component.getByElement($element);

            let isInitialized = Elements.callMethod($element, component, 'isInitialized');
            if (isInitialized && !reinitialize) {
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
        let $elements = $root.find('[data-21-control]');
        for (let element of $elements) {
            let $element = $(element);
            let control = Control.getByElement($element);

            let isInitialized = Elements.callMethod($element, control, 'isInitialized');
            if (isInitialized && !reinitialize) {
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
        return Page.$self.data('21-page');
    }

    static getController() {
        let $content
        if (PageModal.isActive) {
            $content = PageModal.$self;
        } else {
            $content = PageContent.$self;
        }

        let controller = Component.getProperty($content, 'controller');
        return controller;
    }

    static getAction() {
        let $content
        if (PageModal.isActive) {
            $content = PageModal.$self;
        } else {
            $content = PageContent.$self;
        }

        let action = Component.getProperty($content, 'action');
        return action;
    }
}