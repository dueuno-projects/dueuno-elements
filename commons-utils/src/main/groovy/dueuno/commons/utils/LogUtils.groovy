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

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
class LogUtils {

    static String logStackTrace(Exception e) {
        StringWriter sw = new StringWriter()
        PrintWriter pw = new PrintWriter(sw)
        e.printStackTrace(pw)
        return sw.toString()
    }

    static String logStackTrace(List<String> errorArray){
        StringBuilder builder = new StringBuilder()
        for(String s : errorArray) {
            builder.append(s + '\n')
        }
        return builder.toString()
    }

    static String logOperation(String operation, String message = '') {
        return logOperation(operation, null, [], message)
    }

    static String logOperation(String operation, Object object, Closure propertyModifier = {}) {
        return logOperation(operation, object, [], null, propertyModifier)
    }

    static String logOperation(String operation, Object object, List display, Closure propertyModifier = {}) {
        return logOperation(operation, object, display, null, propertyModifier)
    }

    static String logOperation(String operation, Object object, List display, String message, Closure propertyModifier = {}) {
        String formattedMessage = message ? " '${message}' " : ' '
        String formattedProperties = ''

        if (object) {
            Map properties = object.properties.findAll { it.key as String != 'class' }
            List propertyNames = properties.collect { it.key as String }
            List propertyNamesToDisplay = display ?: propertyNames

            propertyModifier.call(properties)

            for (propertyName in propertyNamesToDisplay) {
                String key = StringUtils.camelToScreamingSnake(propertyName as String)
                String value = properties[propertyName]
                formattedProperties += "${key}: '${value}' "
            }
        }

        return "${operation}${formattedMessage}${formattedProperties}"
    }

    static Object benchmark(String name, Boolean enabled = true, Closure codeToBenchmark) {
        def result

        if (!enabled) {
            result = codeToBenchmark.call()

        } else {
            def start = System.currentTimeMillis()
            result = codeToBenchmark.call()
            def now = System.currentTimeMillis()
            if (enabled) {
                LogUtils.log.debug "'$name' execution took: ${now - start} ms"
            }
        }

        return result
    }

}
