// Works only in 'async' functions
// Use: await sleep(i * 1000);
function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

function getOS() {
  var uA = navigator.userAgent || navigator.vendor || window.opera;
  if ((/iPad|iPhone|iPod/.test(uA) && !window.MSStream) || (uA.includes('Mac') && 'ontouchend' in document)) return 'iOS';

  var i, os = ['Windows', 'Android', 'Unix', 'Mac', 'Linux', 'BlackBerry'];
  for (i = 0; i < os.length; i++) if (new RegExp(os[i],'i').test(uA)) return os[i];
}

function enableSimpleBar($element) {
    new SimpleBar($element);
}

function capitalize(val) {
    return val.charAt(0).toUpperCase() + val.slice(1);
}

function isEmpty(obj) {
    if (obj == null) return true;
    if (obj.length > 0) return false;
    if (obj.length === 0) return true;
    if (typeof obj !== "object") return true;
    for (key in obj) {
        if (obj.hasOwnProperty(key)) return false;
    }
    return true;
}

function getEventHandlers(element, targetEvent) {
	const $ = window.jQuery;
	const i = (targetEvent || '').indexOf('.'),
		event = i > -1 ? targetEvent.substr(0, i) : targetEvent,
		namespace = i > -1 ? targetEvent.substr(i + 1) : void(0),
		handlers = Object.create(null);
	element = $(element);
	if (!element.length) return handlers;

	// gets the events associated to a DOM element
	const listeners = $._data(element.get(0), "events") || handlers;
	const events = event ? [event] : Object.keys(listeners);
	if (!targetEvent) return listeners; // Object with all event types

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
