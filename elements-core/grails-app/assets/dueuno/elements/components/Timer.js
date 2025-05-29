class Timer {

    static finalize($element, $root) {
        let enabled = Component.getProperty($element, 'enabled');
        Timer.setEnabled($element, enabled);
    }

    static onInterval($element) {
        let triggerEvent = Component.getEvent($element, 'interval');
        Transition.submit(triggerEvent);
    }

    static setEnabled($element, value) {
        if (value == null) value = true;
        let enabled = Component.getProperty($element, 'enabled');
        let timer = Component.getProperty($element, 'timer');
        if (value == true) {
            if (timer == null) {
                let interval = Component.getProperty($element, 'interval');
                let execute = Component.getProperty($element, 'executeImmediately');
                if (execute == true) {
                    Timer.onInterval($element);
                }
                timer = setInterval(Timer.onInterval, interval, $element);
            }
        } else {
            if (timer != null) {
                clearInterval(timer);
                timer = null;
            }
        }
        Component.setProperty($element, 'timer', timer);
        Component.setProperty($element, 'enabled', value);
    }
}

Component.register(Timer);
