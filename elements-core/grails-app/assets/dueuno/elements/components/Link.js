//= require Label

class Link extends Label {

    static finalize($element, $root) {
        $element.off('click').on('click', Link.onClick);
    }

    static hasTarget(componentEvent) {
        return componentEvent && componentEvent['target'] && componentEvent['target'] != '_self';
    }

    static onClick(event) {
        event.preventDefault();

        let $element = $(event.currentTarget);
        if (Component.getReadonly($element)) {
            return;
        }

        // No event defined
        let componentEventNotToBeAltered = Component.getEvent($element, 'click');
        if (!componentEventNotToBeAltered) {
            return;
        }

        // We copy the defined event to not alter the DOM
        let componentEvent = { ...componentEventNotToBeAltered };

        // CRTL + Click to open on a new tab
        if (event.metaKey || event.ctrlKey) {
            componentEvent['target'] = '_blank';
            componentEvent['direct'] = true;
        }

        // Open a on a different target tab
        if (Link.hasTarget(componentEvent)) {
            let url = Transition.buildUrl(componentEvent);
            let queryString = Transition.buildQueryString(componentEvent);
            window.open(url + queryString, componentEvent['target']);
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

            componentEvent['loading'] = componentEvent['loading'] == null ? true : false;
            componentEvent['direct'] = componentEvent['direct'] == null ? false : true;
            Transition.submit(componentEvent);
        }
    }

}

Component.register(Link);
