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
package dueuno.elements.types

import dueuno.elements.exceptions.ElementsException
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.math.RoundingMode
import java.text.ParseException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
class Types {

    private static Map<String, Class> registry = [:]

    static register(Class type) {
        if (type !in CustomType) {
            throw new ElementsException("Cannot register class '${type}'. Ony classes implementing '${CustomType.getName()}' can be registered as custom types.")
        }

        String typeName = type['TYPE_NAME']
        registry[typeName] = type
    }

    static Boolean isRegistered(Class type) {
        if (type !in CustomType) {
            return false
        }

        return registry[type['TYPE_NAME']]
    }

    private static CustomType create(String typeName) {
        if (registry.containsKey(typeName)) {
            return registry[typeName].getDeclaredConstructor().newInstance() as CustomType

        } else {
            throw new ElementsException("Cannot instantiate custom type '${typeName}', please register the new type (eg. Types.register('CUSTOM_TYPE', CustomType)")
        }
    }

    static Map serialize(Map items) {
        Map results = [:]

        for (item in items) {
            if (item.value in Map) {
                results[item.key] = [type: 'MAP', value: serialize(item.value as Map)]

            } else {
                results[item.key] = serializeValue(item.value)
            }
        }

        return results
    }

    static Map serializeValue(Object value, String valueType = null) {
        if (value == null) {
            return [
                    type: valueType ?: 'UNKNOWN',
                    value: value,
            ]
        }

        Class valueTypeClass = value.getClass()
        if (valueTypeClass in CustomType) {
            return (value as CustomType).serialize()
        }

        switch (value) {
            case Boolean:
                return [
                        type : 'BOOLEAN',
                        value: value,
                ]

            case Number:
                return [
                        type : 'NUMBER',
                        value: value,
                ]

            case String:
                return [
                        type : 'TEXT',
                        value: value,
                ]

            case Map:
                return [
                        type : 'MAP',
                        value: value,
                ]

            case List:
                return [
                        type : 'LIST',
                        value: value,
                ]

            case LocalDateTime:
                return [
                        type : 'DATETIME',
                        value: [
                                year  : (value as LocalDateTime).year,
                                month : (value as LocalDateTime).monthValue,
                                day   : (value as LocalDateTime).dayOfMonth,
                                hour  : (value as LocalDateTime).hour,
                                minute: (value as LocalDateTime).minute,
                        ]
                ]

            case LocalDate:
                return [
                        type : 'DATE',
                        value: [
                                year : (value as LocalDate).year,
                                month: (value as LocalDate).monthValue,
                                day  : (value as LocalDate).dayOfMonth,
                        ]
                ]

            case LocalTime:
                return [
                        type : 'TIME',
                        value: [
                                hour  : (value as LocalTime).hour,
                                minute: (value as LocalTime).minute,
                        ]
                ]

            case Enum:
                return [
                    type: 'TEXT',
                    value: (value as Enum).name(),
                ]

            default:
                return [
                        type: valueType ?: 'UNKNOWN',
                        value: value,
                ]
        }
    }

    static Map deserialize(Map values) {
        Map results = [:]

        for (value in values) {
            String valueName = value.key
            Object valueMap = value.value
            results[valueName] = deserializeValue(valueMap)
        }

        return results
    }

    static Object deserializeValue(Object value) {
        if (value == null) {
            return null
        }

        if (value !in Map) {
            return value
        }

        Map valueMap = value as Map
        try {
            switch (valueMap.type) {
                case 'ID':
                    return deserializeId(valueMap)

                case 'BOOLEAN':
                    return deserializeBoolean(valueMap)

                case 'NUMBER':
                    return deserializeNumber(valueMap)

                case 'TEXT':
                    return deserializeString(valueMap)

                case 'MAP':
                    return deserializeMap(valueMap)

                case 'LIST':
                    return deserializeList(valueMap)

                case 'DATETIME':
                    return deserializeLocalDateTime(valueMap)

                case 'DATE':
                    return deserializeLocalDate(valueMap)

                case 'TIME':
                    return deserializeLocalTime(valueMap)

                case 'UNKNOWN':
                    return valueMap.value

                default:
                    try {
                        CustomType customTypeValue = create(valueMap.type as String)
                        customTypeValue.deserialize(valueMap)
                        return customTypeValue

                    } catch (Exception ignore) {
                        return valueMap.value
                    }
            }

        } catch (Exception e) {
            log.error "Error processing '${valueMap}': ${e.message}"
            return null
        }
    }

    static Long deserializeId(Map valueMap) {
        Long result = valueMap.value as Long
        return result
    }

    static Boolean deserializeBoolean(Map valueMap) {
        Boolean result = valueMap.value as Boolean
        return result
    }

    static BigDecimal deserializeNumber(Map valueMap) {
        BigDecimal result = deserializeBigDecimal(valueMap.value as String, valueMap.decimals as Integer)
        return result
    }

    static String deserializeString(Map valueMap) {
        return valueMap.value
    }

    static Map deserializeMap(Map valueMap) {
        if (valueMap.value !in Map) {
            return [:]
        }

        return deserialize(valueMap.value as Map) ?: [:]
    }

    static List deserializeList(Map valueMap) {
        if (!valueMap) {
            return []
        }

        if (valueMap.value !in List) {
            return []
        }

        return (valueMap.value as List).collect { item ->
            deserializeValue(item)
        }
    }

    static BigDecimal deserializeBigDecimal(String value, Integer decimals) {
        BigDecimal result
        try {
            result = new BigDecimal(value)
            result.setScale(decimals ?: 2, RoundingMode.HALF_UP)

        } catch (ParseException ignore) {
            return null

        } finally {
            return result
        }
    }

    static LocalDate deserializeLocalDate(Map valueMap) {
        Map date = valueMap.value as Map
        Integer day = (Integer) date.day
        Short month = (Short) date.month
        Short year = (Short) date.year

        if (day != null && month != null && year != null) {
            return LocalDate.of(year, month, day)
        }

        return null
    }

    static LocalTime deserializeLocalTime(Map valueMap) {
        Map time = valueMap.value as Map
        Byte hour = (Byte) time.hour
        Byte minute = (Byte) time.minute

        if (hour != null && minute != null) {
            return LocalTime.of(hour, minute)
        }

        return null
    }

    static LocalDateTime deserializeLocalDateTime(Map valueMap) {
        Map date = valueMap.value as Map
        Short year = (Short) date.year
        Short month = (Short) date.month
        Integer day = (Integer) date.day
        Byte hour = (Byte) date.hour
        Byte minute = (Byte) date.minute

        if (day != null && month != null && year != null && hour != null && minute != null) {
            return LocalDateTime.of(year, month, day, hour, minute)
        }

        return null
    }

}
