//= require Label

class Link extends Label {

    static finalize($element, $root) {
        $element.off('keydown').on('keydown', Link.onKeyDown);
        $element.off('mousedown').on('mousedown', Link.onMouseDown);
        $element.off('click').on('click', Link.onClick);
    }

    static onKeyDown(event) {
        let $element = $(event.currentTarget);
    }

    static onMouseDown(event) {
        let $element = $(event.currentTarget);

        if (event.which == 1 && $element.attr('target') == '_blank') {
            let href = $element.attr('href');
            $element.data('href', href);

            let componentEvent = Component.getEvent($element, 'click');
            let queryString = '?' + new URLSearchParams(componentEvent['params']).toString();
            if (queryString == '?') queryString = '';

            $element.attr('href', href + queryString);
        }
    }

    static onClick(event) {
        let $element = $(event.currentTarget);
        let componentEvent = Component.getEvent($element, 'click');

        if (componentEvent && componentEvent['target'] && componentEvent['target'] != '_self') {
            let url = Transition.buildUrl(componentEvent);
            let queryString = Transition.buildQueryString(componentEvent);
            $element.attr('target', componentEvent['target']);
            $element.attr('href', url + queryString);
            return;
        }

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
