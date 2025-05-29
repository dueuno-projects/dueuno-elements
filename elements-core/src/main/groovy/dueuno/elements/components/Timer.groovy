package dueuno.elements.components


import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 */

@CompileStatic
class Timer extends Link {

    Boolean enabled
    Integer interval
    Boolean executeImmediately

    Timer(Map args) {
        super(args)

        enabled = args.enabled == null ? true : args.enabled
        interval = args.interval as Integer ?: 1000
        executeImmediately = args.executeImmediately == null ? true : args.executeImmediately

        eventName = 'interval'
        setOnEvent(args.onInterval as String)
    }

    @Override
    String getPropertiesAsJSON(Map properties = [:]) {
        Map thisProperties = [
                enabled: enabled,
                interval: interval,
                executeImmediately: executeImmediately,
        ]
        return super.getPropertiesAsJSON(thisProperties + properties)
    }
}
