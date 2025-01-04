// Temporary solutions until we get support for static fields
// See: https://github.com/google/closure-compiler/issues/2731
let LoadingScreen_timeoutIdPage = null;
let LoadingScreen_timeoutIdModal = null;

class LoadingScreen {

    static get timeoutIdPage() { return LoadingScreen_timeoutIdPage }
    static set timeoutIdPage(value) { LoadingScreen_timeoutIdPage = value }
    static get timeoutIdModal() { return LoadingScreen_timeoutIdModal }
    static set timeoutIdModal(value) { LoadingScreen_timeoutIdModal = value }

    static page(show, timeout) {
        clearTimeout(LoadingScreen.timeoutIdModal);
        LoadingScreen.timeoutIdModal = null;
        $('#loading-screen-modal').css('display', 'none');

        let $loading = $('#loading-screen-page');
        if (LoadingScreen.timeoutIdPage && !show) {
            clearTimeout(LoadingScreen.timeoutIdPage);
            LoadingScreen.timeoutIdPage = null;
            $loading.css('display', 'none');

        } else if (!LoadingScreen.timeoutIdPage && show) {
            LoadingScreen.timeoutIdPage = setTimeout(() => {
                $loading.css('display', 'block');
            }, timeout);
        }
    }

    static modal(show, timeout) {
        clearTimeout(LoadingScreen.timeoutIdPage);
        LoadingScreen.timeoutIdPage = null;
        $('#loading-screen-page').css('display', 'none');

        let $loading = $('#loading-screen-modal');
        if (LoadingScreen.timeoutIdModal && !show) {
            clearTimeout(LoadingScreen.timeoutIdModal);
            LoadingScreen.timeoutIdModal = null;
            $loading.css('display', 'none');

        } else if (!LoadingScreen.timeoutIdModal && show) {
            LoadingScreen.timeoutIdModal = setTimeout(() => {
                $loading.css('display', 'block');
            }, timeout);
        }
    }

    static show(show, timeout = 350) {
        if (PageModal.isActive) {
            LoadingScreen.modal(show, timeout);
        } else {
            LoadingScreen.page(show, timeout);
        }
    }

}
