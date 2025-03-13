class KeyPress extends Component {

     static finalize($element, $root) {
         $(document)
            .off('keydown.keyPress')
            .on('keydown.keyPress', KeyPress.onKeyPress);
     }

     static onKeyPress(event) {
        let $element = $('[data-21-component="KeyPress"]');
        let $search = $element.find('input');

        event.stopPropagation();

        let triggerKey = Component.getProperty($element, 'triggerKey');
        let readingSpeed = Component.getProperty($element, 'readingSpeed');
        let bufferCleanupTimeout = Component.getProperty($element, 'bufferCleanupTimeout');
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

        let keycode = event.keyCode;
        let valid =
           (keycode > 47 && keycode < 58)   || // number keys
           (keycode == 32)                  || // space bar
           (keycode > 64 && keycode < 91)   || // letter keys
           (keycode > 95 && keycode < 112)  || // numpad keys
           (keycode > 185 && keycode < 193) || // ;=,-./` (in order)
           (keycode > 218 && keycode < 223);   // [\]' (in order)

        if (valid) {
            $search.val($search.val() + event.key);
        }

        if (event.key == triggerKey || triggerKey.length == 0) {
            if ($search.val().length > 0) {
                if (event.target.tagName == 'A') {
                    event.preventDefault();
                }

                let triggerEvent = Component.getEvent($element, 'keypress');
                if (triggerEvent && (event.target.tagName != 'INPUT' || checkMinTime)) {
                    if (!triggerEvent.params) triggerEvent.params = {};
                    triggerEvent.params['_21KeyPressed'] = $search.val();
                    Transition.submit(triggerEvent);
                }
                $search.val('');
            }
        }
     }
}

Component.register(KeyPress);
