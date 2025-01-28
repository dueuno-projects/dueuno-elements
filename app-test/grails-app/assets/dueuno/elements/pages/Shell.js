//= require ShellMenu
//= require ShellNavbar
//= require ShellUserMenu

class Shell extends Page {

    static finalize() {
        Transition.wsConnect();
    }

    static onContentChange() {
        let $navbar = $('body').find('#shell-navbar');
        if (Page.stickyOffset) {
            $navbar.addClass('has-sticky');
        } else {
            $navbar.removeClass('has-sticky');
        }
    }

}

Page.register(Shell);