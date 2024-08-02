//= require Label

class Link extends Label {

    static initialize($element, $root) {
        let componentEvent = Component.getEvent($element, 'click');
        if (componentEvent && componentEvent['direct']) {
            // We disable Bootstrap offcanvas 'data-dismiss-*' since it
            // triggers JS code that overrides ours (see onCLick)
            $element.removeAttr('data-bs-dismiss');
            $element.removeAttr('data-bs-target');
        }
    }

    static finalize($element, $root) {
        $element.off('click').on('click', Link.onClick);
    }

    static onClick(event) {
        let $element = $(event.currentTarget);
        let componentEvent = Component.getEvent($element, 'click');

        if (componentEvent && componentEvent['target'] && componentEvent['target'] != '_self') {
            // This works only when Bootstrap 'data-dismiss-*' attribute is not present
            let url = Transition.buildUrl(componentEvent);
            let queryString = Transition.buildQueryString(componentEvent);
            $element.attr('target', componentEvent['target']);
            $element.attr('href', url + queryString);
            return;
        }

        // From here on we take control
        event.preventDefault();

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
