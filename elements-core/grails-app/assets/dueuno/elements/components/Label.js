class Label extends Component {

    static setText($element, text) {
        $element.find('span').html(text);
    }

    static setReadonly($element, value) {
        // no-op
    }

    static setHtml($element, value) {
        $element.html(value);
    }
}

Component.register(Label);
