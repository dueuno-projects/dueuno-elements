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

import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
class DateUtils {

    static String getCurrentYear() {
        String year = LocalDate.now().year
        return year
    }

    static String getCurrentMonth() {
        String month = LocalDate.now().monthValue
        return month.padLeft(2, '0')
    }

    static String getCurrentDay() {
        String day = LocalDate.now().dayOfMonth
        return day.padLeft(2, '0')
    }

    static String getCurrentHour() {
        String time = LocalTime.now().hour
        return time.padLeft(2, '0')
    }

    static String getCurrentMinute() {
        String time = LocalTime.now().minute
        return time.padLeft(2, '0')
    }

    static String getCurrentSecond() {
        String time = LocalTime.now().second
        return time.padLeft(2, '0')
    }

    static String getFilenameTimestamp() {
        return format(LocalDateTime.now(), 'yyyy-MM-dd-HH-mm-ss')
    }


    // CONVERSIONS
    //
    static Date toDate(LocalDateTime localDateTime) {
        if (!localDateTime) {
            return null
        }

        Instant instant = localDateTime.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
        Date datetime = Date.from(instant)
        return datetime
    }

    static Date toDate(LocalDate localDate) {
        if (!localDate) {
            return null
        }

        Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
        Date date = Date.from(instant)
        return date
    }

    static LocalDateTime toLocalDateTime(Date datetime) {
        if (!datetime) {
            return null
        }

        Instant instant = Instant.ofEpochMilli(datetime.getTime())
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return localDateTime
    }

    static LocalDate toLocalDate(Date date) {
        if (!date) {
            return null
        }

        Instant instant = Instant.ofEpochMilli(date.getTime())
        LocalDate localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate()
        return localDate
    }

    static LocalTime toLocalTime(Date date) {
        if (!date) {
            return null
        }

        Instant instant = Instant.ofEpochMilli(date.getTime())
        LocalTime localTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalTime()
        return localTime
    }



    // FORMATTING
    //
    static String format(LocalDateTime dateTime, String pattern = "yyyy-MM-dd'T'HH:mm") {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern)
        return dateTime.format(dtf)
    }

    static String format(LocalDate date, String pattern = "yyyy-MM-dd") {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern)
        return date.format(dtf)
    }

    static String format(LocalTime time, String pattern = "HH:mm") {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern)
        return time.format(dtf)
    }

    static String format(Date date, String pattern = "yyyy-MM-dd") {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern)
        return simpleDateFormat.format(date)
    }

    static String reformat(String date, String patternFrom, String patternTo) {
        if (!date) {
            return ''
        }

        LocalDate localDate = parseLocalDate(date, patternFrom)
        return format(localDate, patternTo)
    }



    // PARSING
    //
    static LocalDateTime parseLocalDateTime(String dateTime, String pattern = 'dd/MM/yyyy HH:mm') {
        if (!dateTime) {
            return null
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern)
        return LocalDateTime.parse(dateTime, dtf)
    }

    static LocalDate parseLocalDate(String date, String pattern = 'dd/MM/yyyy') {
        if (!date) {
            return null
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern)
        return LocalDate.parse(date, dtf)
    }

    static LocalTime parseLocalTime(String time, String pattern = 'HH:mm') {
        if (!time) {
            return null
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern)
        return LocalTime.parse(time, dtf)
    }

    static Date parseDate(String date, String pattern = 'dd/MM/yyyy') {
        if (!date) {
            return null
        }

        SimpleDateFormat sdf = new SimpleDateFormat(pattern)
        return sdf.parse(date)
    }

}
