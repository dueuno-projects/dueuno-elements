//= require PageBlank

class PageWebsocket extends PageBlank {

    static finalize() {
        Transition.wsConnect();
    }

}

Page.register(PageWebsocket);
