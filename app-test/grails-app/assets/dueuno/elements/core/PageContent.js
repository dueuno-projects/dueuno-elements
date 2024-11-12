// Temporary solutions until we get support for static fields
// See: https://github.com/google/closure-compiler/issues/2731
let PageContent_$self = null;
let PageContent_$scrollbar = null;
let PageContent_$scrollableElements = [];

class PageContent {

    static get $self() { return PageContent_$self }
    static set $self(value) { PageContent_$self = value }
    static get $scrollbar() { return PageContent_$scrollbar }
    static set $scrollbar(value) { PageContent_$scrollbar = value }
    static get $scrollableElements() { return PageContent_$scrollableElements }
    static set $scrollableElements(value) { PageContent_$scrollableElements = value }

    static initialize() {
        PageContent.$self = $('#page-content');
        PageContent.$scrollbar = $('#page-content-scrollbar');
    }

    static finalize() {
        let content = PageContent.$self[0].getBoundingClientRect();
        PageContent.$scrollbar.css({left: content.left, width: content.width});
        PageContent.attachCurrentScrollableElement();

        PageContent.$scrollbar.off('scroll').on('scroll', PageContent.onScrollbarScroll);
        $(window).on('scroll', PageContent.onWindowScroll);
        $(window).on('resize', PageContent.onWindowResize);
    }

    static onWindowResize(event) {
        let $element = $(event.currentTarget);
        let contentRect = PageContent.$self[0].getBoundingClientRect();
        PageContent.$scrollbar.css({left: contentRect.left, width: contentRect.width});
        PageContent.attachCurrentScrollableElement();
    }

    static onWindowScroll(event) {
        PageContent.attachCurrentScrollableElement();
    }

    static onScrollbarScroll(event) {
        let $element = $(event.currentTarget);
        let scrollLeft = $element[0].scrollLeft;

        let $currentScrollableElement = PageContent.getCurrentScrollableElement();
        if ($currentScrollableElement) {
            $currentScrollableElement.container.scrollLeft(scrollLeft);
        }
    }

    static addScrollableElement($content, $container) {
        PageContent.$scrollableElements.push({
            content: $content,
            container: $container,
        });
    }

    static getCurrentScrollableElement() {
        let height = window.innerHeight;

        for (let $scrollable of PageContent.$scrollableElements) {
            let scrollableRect = $scrollable.content[0].getBoundingClientRect();
            if (scrollableRect.top < height && scrollableRect.bottom > height - 10) {
                return $scrollable;
            }
        }

        return null;
    }

    static attachScrollableElement($element) {
        let $scrollbarContentMirror = PageContent.$scrollbar.find('div');
        $scrollbarContentMirror.width($element.content.width());
        PageContent.scrollElement($element.container[0].scrollLeft);
    }

    static attachCurrentScrollableElement() {
        let $currentScrollableElement = PageContent.getCurrentScrollableElement();
        if ($currentScrollableElement) {
            PageContent.attachScrollableElement($currentScrollableElement);
            PageContent.$scrollbar.show();

        } else {
            PageContent.$scrollbar.hide();
        }
    }

    static scrollElement(scrollLeft) {
        PageContent.$scrollbar.scrollLeft(scrollLeft);
    }

}