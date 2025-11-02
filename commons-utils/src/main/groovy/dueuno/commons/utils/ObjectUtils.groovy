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
class ObjectUtils {

    /**
     * Returns the value of a field in an object from its name in dotted path format descending recursively
     * into sub-objects. Eg: 'person.address.street'
     *
     * @param propertyName Dotted path name of the field
     * @param object The object containing the field
     *
     * @return The field value; 'null' if not found
     */
    static Object getValue(Object object, String propertyName) {
        if (!object) {
            return null
        }

        String[] fieldNameList = propertyName.split('\\.')
        if (fieldNameList.size() == 1) {
            return getValueOrThrowException(object, fieldNameList[0])

        } else {
            String nextFieldName = fieldNameList.tail().join('.') ?: [:]
            Object nextValues = getValueOrThrowException(object, fieldNameList[0])
            return getValue(nextValues, nextFieldName)
        }
    }

    private static Object getValueOrThrowException(Object object, String fieldName) {
        try {
            Object value = object[fieldName]
            return value

        } catch (MissingPropertyException ignore) {
            return null

        } catch (Exception e) {
            throw new Exception(
                    "Cannot read property '${fieldName}' from object '${object.getClass().getName()}'. " +
                            "If this is a domain object please make sure to set eager fetching" +
                            "for each field that you want to access whose type is a domain class " +
                            "(eg. DomainClass.list(fetch: [fieldName: 'join']): " +
                            e)
        }
    }

}
