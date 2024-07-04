class Log {
    static error(message) {
        if (_21_.log !== undefined && _21_.log.error) {
            console.error(message);
        }
    }

    static debug(message) {
        if (_21_.log !== undefined && _21_.log.debug) {
            console.log(message);
        }
    }

    static trace(message) {
        if (_21_.log !== undefined && _21_.log.trace) {
            console.log(message);
        }
    }
}
