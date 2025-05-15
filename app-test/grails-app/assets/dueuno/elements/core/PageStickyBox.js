// Temporary solutions until we get support for static fields
// See: https://github.com/google/closure-compiler/issues/2731
let PageStickyBox_top = 0;
let PageStickyBox_offset = 0;

class PageStickyBox {

    static get top() { return PageStickyBox_top }
    static set top(value) { PageStickyBox_top = value }
    static get offset() { return PageStickyBox_offset }
    static set offset(value) { PageStickyBox_offset = value }

    static get $self() { return $('#page-sticky-box') }

    static initialize() {
        if (PageModal.isActive) {
            return;
        }

        PageStickyBox.offset = PageStickyBox.top;

        let $components = PageContent.$self.find('> [data-21-component]');
        for (let component of $components) {
            let $component = $(component);
            let properties = Component.getProperties($component);
            if (properties['sticky']) {
                PageStickyBox.setSticky($(component));
            }
        }

        let $stickyComponents = PageContent.$self.find('> [data-21-component][sticky]');
        if ($stickyComponents.exists()) {
            PageStickyBox.$self.empty();
            PageStickyBox.$self.append($stickyComponents);
            PageContent.$self.prepend(PageStickyBox.$self);
            PageStickyBox.setActive();

        } else if (!PageStickyBox.isActive() && !$stickyComponents.exists()) {
            PageStickyBox.$self.empty();
        }
    }

    static finalize() {
        if (PageModal.isActive) {
            return;
        }

        PageContent.$self.before(PageStickyBox.$self);

        // Sometimes the offest is one line bigger then needed, this trick
        // of of -.3 reduces the cases to almost none
        let offsetHeight = PageStickyBox.$self[0].offsetHeight - .3;
        PageStickyBox.offset = PageStickyBox.top + offsetHeight;
        PageContent.$self.css('padding-top', offsetHeight);
    }

    static setActive() {
        PageContent.$self[0].setAttribute('has-sticky', '');
    }

    static isActive() {
        return PageContent.$self[0].hasAttribute('has-sticky', '');
    }

    static getContentStickyComponents() {
        let $contentSticky = PageContent.$self.find('> [data-21-component][sticky]');
        return $contentSticky;
    }

    static setSticky($element) {
        $element[0].setAttribute('sticky', '');
    }

    static setTop($element) {
        PageStickyBox.top = $element[0].offsetHeight;
    }

}