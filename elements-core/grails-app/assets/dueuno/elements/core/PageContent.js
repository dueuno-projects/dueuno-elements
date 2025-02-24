// Temporary solutions until we get support for static fields
// See: https://github.com/google/closure-compiler/issues/2731
let PageContent_$self = null;
let PageContent_$scrollableElements = [];
let PageContent_$scrollbarBox = null;
let PageContent_$scrollbar = null;

class PageContent extends Component {

    static get $self() { return PageContent_$self }
    static set $self(value) { PageContent_$self = value }
    static get $scrollableElements() { return PageContent_$scrollableElements }
    static set $scrollableElements(value) { PageContent_$scrollableElements = value }
    static get $scrollbarBox() { return PageContent_$scrollbarBox }
    static set $scrollbarBox(value) { PageContent_$scrollbarBox = value }
    static get $scrollbar() { return PageContent_$scrollbar }
    static set $scrollbar(value) { PageContent_$scrollbar = value }

    static initialize() {
        PageContent.$self = $('#page-content');
        PageContent.$scrollbarBox = $('#page-content-scrollbar-box');
        PageContent.$scrollbar = $('#page-content-scrollbar');
    }

    static finalize() {
        if (!PageContent.$self.exists()) {
            return;
        }

        // Initializing Bootstrap tooltips
        let tooltipTriggerList = $('[data-bs-toggle="tooltip"]');
        for (let tooltipTrigger of tooltipTriggerList) {
            new bootstrap.Tooltip(tooltipTrigger);
        }

        let content = PageContent.$self[0].getBoundingClientRect();
        PageContent.$scrollbarBox.css({left: content.left, width: content.width});
        PageContent.updateScrollbar();

        PageContent.$scrollbar.off('scroll').on('scroll', PageContent.onScrollbarScroll);
        $(window).on('scroll', PageContent.onWindowScroll);
        $(window).on('resize', PageContent.onWindowResize);
    }

    static onWindowResize(event) {
        let contentRect = PageContent.$self[0].getBoundingClientRect();
        PageContent.$scrollbarBox.css({left: contentRect.left, width: contentRect.width});
        PageContent.updateScrollbar();
    }

    static onWindowScroll(event) {
        PageContent.updateScrollbar();
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
            if (scrollableRect.top + 20 < height && scrollableRect.bottom > height - 20) {
                return $scrollable;
            }
        }

        return null;
    }

    static updateScrollbar() {
        if (getOS() == 'iOS') {
            // No scrollbars on iOS, it has a glitch that brakes smooth scrolling
            return;
        }

        let $currentScrollableElement = PageContent.getCurrentScrollableElement();
        let isScrollbarRequired = $currentScrollableElement && $currentScrollableElement.content.width() > $currentScrollableElement.container.width();

        if (isScrollbarRequired) {
            PageContent.$scrollbarBox.show(); // The 'show' must stay before the 'setScroll' (don't ask me why)
            PageContent.setScroll($currentScrollableElement.container, $currentScrollableElement.container[0].scrollLeft);

        } else {
            PageContent.$scrollbarBox.hide();
        }
    }

    static setScroll($container, scrollLeft) {
        let $currentScrollableElement = PageContent.getCurrentScrollableElement();
        if ($currentScrollableElement && $container.is($currentScrollableElement.container)) {
            let $scrollbarContentMirror = $('#page-content-scrollbar-content-mirror');
            // Shrank to accomodate the container margins defined in the CSS (#page-content-scrollbar-box)
            $scrollbarContentMirror.width($currentScrollableElement.content.width() * 0.99);
            PageContent.$scrollbar.scrollLeft(scrollLeft);
        }
    }

}

Component.register(PageContent);