class KeyPress extends Component {

    static finalize($element, $root) {
        $(document)
            .off('keydown.keyPress')
            .on('keydown.keyPress', KeyPress.onKeyPress);

        log.debug('Finalizing "' + Component.getId($element) + '"')
        log.events($(document));
    }

    static onKeyPress(event) {
        let $element = $('[data-21-component="KeyPress"]');
        let $search = $element.find('input');

        event.stopPropagation();

        let triggerKey = Component.getProperty($element, 'triggerKey');
        let readingSpeed = Component.getProperty($element, 'readingSpeed');
        let bufferCleanupTimeout = Component.getProperty($element, 'bufferCleanupTimeout');
        let keepClean = Component.getProperty($element, 'keepClean');
        let timer = Component.getProperty($element, 'timer');

        if (triggerKey == null) {
            return;
        }

        if (isNaN(timer)) timer = 0;
        let now = Date.now();
        let timeInterval = now - timer;
        Component.setProperty($element, 'timer', now);

        let checkMinTime = (readingSpeed == 0 || timeInterval < readingSpeed);
        let checkMaxTime = (bufferCleanupTimeout == 0 || timeInterval > bufferCleanupTimeout);

        if (checkMaxTime) {
            $search.val('');
        }

        let printable = KeyPress.isPrintable(event.keyCode);
        let isModifierPressed = KeyPress.isModifierPressed(event);

        if (triggerKey.length == 0) {
            $search.val(event.key);
        } else {
            if (printable) {
                $search.val($search.val() + event.key);
            }
        }

        if (keepClean && !isModifierPressed && checkMinTime && event.target.tagName == 'INPUT') {
            event.preventDefault();
            $(event.target).val('');
        }

        if ((event.key == triggerKey && $search.val().length > 0 && checkMinTime) || triggerKey.length == 0) {
            if (triggerKey.length > 0 && $search.val().length > 0 &&
                (event.target.tagName == 'A' || event.target.tagName == 'BUTTON')) {
                event.preventDefault();
            }

            let triggerEvent = Component.getEvent($element, 'keypress');
            if (triggerEvent && (event.target.tagName != 'INPUT' || !printable || checkMinTime)) {
                if (!triggerEvent.params) triggerEvent.params = {};
                triggerEvent.params['_21KeyPressed'] = $search.val();
                Transition.submit(triggerEvent);
            }
            $search.val('');
        }
    }

    static isPrintable(keycode) {
        let printable =
            (keycode > 47 && keycode < 58)   || // number keys
            (keycode == 32)                  || // space bar
            (keycode > 64 && keycode < 91)   || // letter keys
            (keycode > 95 && keycode < 112)  || // numpad keys
            (keycode > 185 && keycode < 193) || // ;=,-./` (in order)
            (keycode > 218 && keycode < 223);   // [\]' (in order)
        return printable;
    }

    static isModifierPressed(event) {
        return event.shiftKey || event.ctrlKey || event.altKey || event.getModifierState('AltGraph');
    }
}

Component.register(KeyPress);
