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
package dueuno.elements.core

import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 */

@CompileStatic
class PrettyPrinterProperties implements Serializable {
    Object prettyPrinter
    String transformer

    Locale locale
    Boolean renderTextPrefix
    String textPrefix
    List textArgs

    Boolean renderBoolean       // Implemented in Label
    Boolean highlightNegative   // Implemented in Label
    String renderZero
    Boolean renderDate
    String renderDatePattern
    Boolean renderTime
    Boolean renderSeconds
    String renderDelimiter

    Integer decimals
    String decimalFormat
    Boolean prefixedUnit
    Boolean symbolicCurrency
    Boolean symbolicQuantity
    Boolean invertedMonth
    Boolean twelveHours
    Boolean firstDaySunday

    PrettyPrinterProperties(Map args = [:]) {
        prettyPrinter = args.prettyPrinter
        transformer = args.transformer
        locale = args.locale as Locale
        renderTextPrefix = args.renderTextPrefix
        textPrefix = args.textPrefix
        textArgs = args.textArgs as List
        renderBoolean = args.renderBoolean
        highlightNegative = args.highlightNegative
        renderZero = args.renderZero
        renderDate = args.renderDate
        renderDatePattern = args.renderDatePattern
        renderTime = args.renderTime
        renderSeconds = args.renderSeconds
        renderDelimiter = args.renderDelimiter
        decimals = args.decimals as Integer
        decimalFormat = args.decimalFormat
        prefixedUnit = args.prefixedUnit
        symbolicCurrency = args.symbolicCurrency
        symbolicQuantity = args.symbolicQuantity
        invertedMonth = args.invertedMonth
        twelveHours = args.twelveHours
        firstDaySunday = args.firstDaySunday
    }

    void set(Map properties) {
        for (property in properties) {
            setProperty(property.key as String, property.value)
        }
    }

    void setTextPrefix(String value) {
        textPrefix = value
        renderTextPrefix = true
    }
}