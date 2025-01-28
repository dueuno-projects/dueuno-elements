//= require TextField

class Textarea extends TextField {

    static finalize($element, $root) {
        TextField.finalize($element, $root);
        $element.off('keypress').on('keypress', Textarea.onKeyPress);
    }

    static onKeyPress(event) {
        let $element = $(event.currentTarget);
        let properties = Component.getProperties($element);
        if (properties.acceptNewLine && event.key == 'Enter') {
            // Enter never triggers an event since it is a valid value
            // in multiline text
            return;
        }

        TextField.onKeyPress(event);
    }

}

Control.register(Textarea);
