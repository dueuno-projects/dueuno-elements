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
package dueuno.elements.utils

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
class LocaleUtils {

    static void setLocalizedProperty(Object obj, String propertyName, String value, String language = null) {
        if (!language) {
            obj[propertyName + '_en'] = value
        }

        Boolean prop = hasProperty(propertyName + '_' + language)
        if (prop) {
            obj[propertyName + '_' + language] = value
        } else {
            log.error "NOT IMPLEMENTED: Cannot set '${propertyName}' with locale '${language}', please contact the developers."
        }
    }

    static Object getLocalizedProperty(Object obj, String propertyName, String language) {
        def hasProperty = hasLocalizedProperty(obj, propertyName, language)
        def defaultValue = obj[propertyName + '_en']
        def localizedValue = null

        if (hasProperty) {
            localizedValue = obj[propertyName + '_' + language]
        }

        return localizedValue ?: defaultValue
    }

    static Boolean hasLocalizedProperty(Object obj, String propertyName, String language) {
        return obj.hasProperty(propertyName + '_' + language)
    }

}
