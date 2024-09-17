//= require Label

class Link extends Label {

    static finalize($element, $root) {
        $element.off('click').on('click', Link.onClick);
    }

    static hasTarget(componentEvent) {
        return componentEvent && componentEvent['target'] && componentEvent['target'] != '_self';
    }

    static onClick(event) {
        let $element = $(event.currentTarget);
        let componentEvent = Component.getEvent($element, 'click');
        let componentEventTemp = { ...componentEvent };

        // From here on we take control
        event.preventDefault();
        Component.setReadonly($element, true);
        setTimeout(() => {
            Component.setReadonly($element, false);
        }, 200);

        if (event.metaKey || event.ctrlKey) {
            componentEventTemp['target'] = '_blank';
            componentEventTemp['direct'] = true;
        }

        if (Link.hasTarget(componentEventTemp)) {
            let url = Transition.buildUrl(componentEventTemp);
            let queryString = Transition.buildQueryString(componentEventTemp);
            window.open(url + queryString, componentEventTemp['target']);
            return;
        }

        if (!componentEvent) {
            return;
        }

        if (Component.getReadonly($element)) {
            return;
        }

        if (componentEvent['confirmMessage']) {
            let option2EventData = Object.assign({}, componentEvent);
            PageMessageBox.confirm(null, {
                confirmMessage: option2EventData.confirmMessage,
                option2Click: option2EventData,
            });

        } else if (componentEvent['infoMessage']) {
            PageMessageBox.info(null, componentEvent);

        } else {
            if (componentEvent.renderProperties) {
                componentEvent.renderProperties['updateUrl'] = true;
            }

            Transition.submit(componentEvent);
        }
    }
}

Component.register(Link);
