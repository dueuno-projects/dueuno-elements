class Timer {

    static initialize($element, $root) {
        let enabled = Component.getProperty($element, 'enabled');
        Timer.setEnabled($element, enabled);
    }

    static isInitialized($element) {
        return false;
    }

    static deactivate($element) {
        let timerId = Component.getProperty($element, 'timerId');
        if (timerId != null) {
            clearInterval(timerId);
            let contentTimerIds = Timer.getContentTimerIds();
            let index = contentTimerIds.indexOf(timerId);
            if (index >= 0) {
                contentTimerIds.splice(index, 1);
                Timer.setContentTimerIds(contentTimerIds);
            }
            Component.setProperty($element, 'timerId', null);
        }
    }

    static onInterval($element) {
        let triggerEvent = Component.getEvent($element, 'interval');
        Transition.submit(triggerEvent);
    }

    static setEnabled($element, value) {
        if (value == null) value = true;
        let enabled = Component.getProperty($element, 'enabled');
        let timerId = Component.getProperty($element, 'timerId');

        if (value == true) {
            if (timerId == null) {
                let interval = Component.getProperty($element, 'interval');
                let execute = Component.getProperty($element, 'executeImmediately');
                if (execute == true) {
                    Timer.onInterval($element);
                }
                timerId = setInterval(Timer.onInterval, interval, $element);

                let contentTimerIds = Timer.getContentTimerIds();
                if (contentTimerIds.indexOf(timerId) < 0) {
                    contentTimerIds.push(timerId);
                    Timer.setContentTimerIds(contentTimerIds);
                }
                Component.setProperty($element, 'timerId', timerId);
            }
        } else {
            Timer.deactivate($element);
        }
        Component.setProperty($element, 'enabled', value);
    }

    static getContentTimerIds() {
        let $content = Page.getActiveContent();
        let contentTimerIds = Component.getProperty($content, 'contentTimerIds');
        if (contentTimerIds == null) contentTimerIds = '[]';
        return JSON.parse(contentTimerIds);
    }

    static setContentTimerIds(contentTimerIds) {
        let $content = Page.getActiveContent();
        Component.setProperty($content, 'contentTimerIds', JSON.stringify(contentTimerIds));
    }
}

Component.register(Timer);
