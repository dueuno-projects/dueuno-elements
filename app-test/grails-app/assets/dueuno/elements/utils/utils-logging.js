const log = {
    error: function(message) {
        if (_21_.log !== undefined && _21_.log.error) {
            console.error(message);
        }
    },

    debug: function(message) {
        if (_21_.log !== undefined && _21_.log.debug) {
            console.log(message);
        }
    },

    trace: function(message) {
        if (_21_.log !== undefined && _21_.log.trace) {
            console.log(message);
        }
    }
}
