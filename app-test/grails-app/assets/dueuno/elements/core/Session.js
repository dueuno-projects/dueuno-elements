class Session {

    static setForGlobal(key, value) {
        Session.set('GLOBAL', null, null, key, value);
    }

    static getForGlobal(key) {
        Session.get('GLOBAL', null, null, key);
    }

    static setForController(controller, key, value) {
        Session.set('CONTROLLER', controller, null, key, value);
    }

    static getForController(controller, key) {
        Session.get('CONTROLLER', controller, null, key);
    }

    static setForAction(controller, action, key, value) {
        Session.set('ACTION', controller, action, key, value);
    }

    static getForAction(controller, action, key) {
        Session.get('ACTION', controller, action, key);
    }

    static set(scope, controller, action, key, value) {
        Transition.submit({
            controller: 'session',
            action: 'set',
            params: {
                scope: scope,
                targetController: controller,
                targetAction: action,
                key: key,
                value: value,
            }
        });
    }

    static get(scope, controller, action, key) {
        return Transition.submit({
            controller: 'session',
            action: 'get',
            params: {
                scope: scope,
                targetController: controller,
                targetAction: action,
                key: key,
            }
        });
    }
}