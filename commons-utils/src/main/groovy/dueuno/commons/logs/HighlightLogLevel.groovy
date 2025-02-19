package dueuno.commons.logs

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.color.ANSIConstants
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase
import groovy.transform.CompileStatic

@CompileStatic
class HighlightLogLevel extends ForegroundCompositeConverterBase<ILoggingEvent> {

    @Override
    protected String getForegroundColorCode(ILoggingEvent event) {
        Level level = event.getLevel()

        switch (level.toInt()) {
            case Level.ERROR_INT:
                return ANSIConstants.RED_FG

            case Level.WARN_INT:
                return ANSIConstants.YELLOW_FG

            default:
                return ANSIConstants.DEFAULT_FG
        }
    }

}