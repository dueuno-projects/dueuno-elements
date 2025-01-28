class Log {
    error(message) {
        if (_21_.log !== undefined && _21_.log.error) {
            console.error(message);
        }
    }

    debug(message) {
        if (_21_.log !== undefined && _21_.log.debug) {
            console.log(message);
        }
    }

    trace(message) {
        if (_21_.log !== undefined && _21_.log.trace) {
            console.log(message);
        }
    }
}

const log = new Log()
