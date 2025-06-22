/*
 * Copyright 2021 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dueuno.commons.utils

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
class TimeUtils {

    static Duration elapsed(LocalTime timeStart, LocalTime timeEnd) {
        if (!timeEnd) {
            timeEnd = timeStart
        }

        LocalDate aDay = LocalDate.of(2021,1,1)
        LocalDateTime aDayIn = aDay.atTime(timeStart)
        LocalDateTime aDayOut = aDay.atTime(timeEnd)

        Duration elapsed = Duration.between(aDayIn, aDayIn > aDayOut ? aDayOut.plusDays(1) : aDayOut)
        return elapsed
    }

    static Duration elapsed(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd) {
        if (!dateTimeEnd) {
            dateTimeEnd = dateTimeStart
        }

        Duration elapsed = Duration.between(dateTimeStart, dateTimeEnd)
        return elapsed
    }

    static String renderElapsedSeconds(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd) {
        Long elapsedMin = elapsed(dateTimeStart, dateTimeEnd).toMillis() / 1000 as Long
        return renderElapsedSeconds(elapsedMin)
    }

    static String renderElapsedSeconds(LocalTime timeStart, LocalTime timeEnd) {
        Long elapsedMin = elapsed(timeStart, timeEnd).toMillis() / 1000 as Long
        return renderElapsedSeconds(elapsedMin)
    }

    static String renderElapsedSeconds(Long seconds) {
        if (!seconds) {
            return ''
        }

        Long elapsedHour = seconds / 3600 as Long
        Long elapsedHourMin = (seconds / 60 as Long) - (elapsedHour * 60)
        Long elapsedHourSec = seconds % 60
        return "${elapsedHour}:${elapsedHourMin.toString().padLeft(2, '0')}:${elapsedHourSec.toString().padLeft(2, '0')}"
    }

    static String renderElapsedMinutes(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd) {
        Long elapsedMin = elapsed(dateTimeStart, dateTimeEnd).toMinutes()
        return renderElapsedMinutes(elapsedMin)
    }

    static String renderElapsedMinutes(LocalTime timeStart, LocalTime timeEnd) {
        Long elapsedMin = elapsed(timeStart, timeEnd).toMinutes()
        return renderElapsedMinutes(elapsedMin)
    }

    static String renderElapsedMinutes(Long minutes) {
        if (!minutes) {
            return ''
        }

        Long elapsedHour = minutes / 60 as Long
        Long elapsedHourMin = minutes % 60
        return "${elapsedHour}:${elapsedHourMin.toString().padLeft(2, '0')}"
    }

}
