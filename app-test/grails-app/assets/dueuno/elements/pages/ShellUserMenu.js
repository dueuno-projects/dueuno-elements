class ShellUserMenu {

    static initialize($element, $root) {
        let $menu = $('#shell-user-menu-items');
        new SimpleBar($menu[0]);
    }

    static finalize($element, $root) {
        if (!_21_.user.animations) {
            $element.off('shown.bs.offcanvas').on('shown.bs.offcanvas', ShellUserMenu.onShown);
        }
    }

    static onShown(event) {
        let $element = $(event.currentTarget);
        let $offcanvas = $('body').find('.offcanvas-backdrop');
        $offcanvas.removeClass('fade');
    }
}

Component.register(ShellUserMenu);