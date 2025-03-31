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

    events($element, eventName = null) {
        if (_21_.log !== undefined && _21_.log.debug) {
            let componentName = Component.getId($element) ?? $element[0];
            console.log('EVENTS of "' + componentName + '":');
            console.log(getEventHandlers($element, eventName));
        }
    }

    trace(message) {
        if (_21_.log !== undefined && _21_.log.trace) {
            console.log(message);
        }
    }
}

const log = new Log()

function getEventHandlers($element, eventName) {
	const i = (eventName || '').indexOf('.'),
		event = i > -1 ? eventName.substr(0, i) : eventName,
		namespace = i > -1 ? eventName.substr(i + 1) : void(0),
		handlers = Object.create(null);

	if (!$element.length) return handlers;

	// gets the events associated to a DOM element
	const listeners = $._data($element.get(0), "events") || handlers;
	const events = event ? [event] : Object.keys(listeners);
	if (!eventName) return listeners; // Object with all event types

	events.forEach((type) => {
		// gets event-handlers by event-type or namespace
		(listeners[type] || []).forEach(getHandlers, type);
	});

	// eslint-disable-next-line
	function getHandlers(e) {
		const type = this.toString();
		const eNamespace = e.namespace || (e.data && e.data.handler);

		// gets event-handlers by event-type or namespace
		if ((event === type && !namespace) ||
			(eNamespace === namespace && !event) ||
			(eNamespace === namespace && event === type)) {

			handlers[type] = handlers[type] || [];
			handlers[type].push(e);
		}
	}

	return handlers;
}
