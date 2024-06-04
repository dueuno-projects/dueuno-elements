class KeyPress extends Component {

     static finalize($element, $root) {
         $(document)
            .off('keypress.keyPress')
            .on('keypress.keyPress', '*', KeyPress.onKeyPress);
     }

     static onKeyPress(event) {
        let $element = $('[data-21-component="KeyPress"]');
        let $search = $element.find('input');

        event.stopPropagation();

        let triggerKey = Component.getProperty($element, 'triggerKey');
        if (triggerKey == null) {
            return;
        }

        if (event.key != 'Enter' || triggerKey.length == 0) {
            $search.val($search.val() + event.key);
        }

        if (event.key == triggerKey || triggerKey.length == 0) {
            let triggerEvent = Component.getEvent($element, 'keypress');
            if (triggerEvent) {
                if (!triggerEvent.params) triggerEvent.params = {};
                triggerEvent.params['_21KeyPressed'] = $search.val();
                Transition.submit(triggerEvent);
                $search.val('');
            }
        }
     }
}

Component.register(KeyPress);
