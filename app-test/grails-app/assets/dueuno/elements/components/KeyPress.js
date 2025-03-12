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
        if (triggerKey == null) {
            return;
        }

        if (isNaN(this.timer)) this.timer = 0;
        let t = Date.now();
        let timeInterval = t - this.timer;
        this.timer = t;

        //a barcode reader is typically much faster at typing than a human...
        //we use this principle to understand if the typing comes from a reader
        let checkMinTime = (timeInterval < 15);

        //to prevent accidental typing, the buffer empties after a certain time
        let checkMaxTime = (timeInterval > 1500);
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
