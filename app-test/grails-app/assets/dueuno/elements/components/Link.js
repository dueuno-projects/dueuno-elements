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

        // From here on we take control
        event.preventDefault();

        if (event.metaKey || event.ctrlKey) {
            componentEvent['target'] = '_blank';
            componentEvent['direct'] = true;
        }

        if (Link.hasTarget(componentEvent)) {
            let url = Transition.buildUrl(componentEvent);
            let queryString = Transition.buildQueryString(componentEvent);
            window.open(url + queryString, componentEvent['target']);
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
