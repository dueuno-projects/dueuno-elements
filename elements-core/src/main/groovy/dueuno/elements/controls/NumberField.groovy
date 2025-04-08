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
package dueuno.elements.controls

import dueuno.elements.core.Elements
import dueuno.elements.types.Type
import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class NumberField extends TextField {

    Integer decimals
    Boolean negative
    Integer min
    Integer max

    NumberField(Map args) {
        super(args)

        viewTemplate = 'TextField'
        valueType = Type.NUMBER
        pattern = args.pattern ?: '^[0-9\\-\\.\\,]*$'
        decimals = args.decimals as Integer ?: 0
        negative = (args.negative == null) ? true : args.negative
        min = args.min as Integer
        max = args.max as Integer

        inputType = TextFieldInputType.TEXT
        inputMode = decimals ? TextFieldInputMode.DECIMAL : TextFieldInputMode.NUMERIC
    }

    String buildPattern() {
        String result = '^'

        if (negative) result += '-?'

        result += '[0-9]*'
        if (decimals > 0) {
            result += '((?<=[0-9])[\\.\\,])?[0-9]{0,' + decimals + '}';
        }

        result += '$'
        return result
    }

    @Override
    String getPropertiesAsJSON(Map properties = [:]) {
        pattern = buildPattern()
        Map thisProperties = [
                decimals: decimals,
                negative: negative,
                min: min,
                max: max,
        ]
        return super.getPropertiesAsJSON(thisProperties + properties)
    }

    @Override
    String getValueAsJSON() {
        Map valueMap = [
                type: valueType,
                value: value,
                decimals: decimals,
        ]

        return Elements.encodeAsJSON(valueMap)
    }
}
