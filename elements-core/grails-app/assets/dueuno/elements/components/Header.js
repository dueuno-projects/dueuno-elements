class Header extends Component {

    static setTitle($element, value) {
        $element.find('.component-header-title').html(value);
    }
}

Component.register(Header);