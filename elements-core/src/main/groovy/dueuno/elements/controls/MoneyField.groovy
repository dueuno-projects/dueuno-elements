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
import dueuno.elements.exceptions.ElementsException
import dueuno.elements.types.Money
import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class MoneyField extends NumberField {

    MoneyField(Map args) {
        super(args)

        viewTemplate = 'MoneyField'
        valueType = 'MONEY'

        decimals = args.decimals == null ? 2 : args.decimals as Integer
        negative = (args.negative == null) ? false : args.negative
        prefix = args.currency as String ?: 'EUR'

        inputMode = args.inputMode as TextFieldInputMode ?: (decimals ? TextFieldInputMode.DECIMAL : TextFieldInputMode.NUMBER)
    }

    @Override
    void setValue(Object value, Boolean transform = true) {
        super.setValue(value, transform)

        if (this.value && (this.value !instanceof Money)) {
            throw new ElementsException("${this.getClass().simpleName} can only accept values of type '${Money.name}'")
        }

        if (this.value) {
            prefix = (this.value as Money).currency
        }
    }

    @Override
    String getValueAsJSON() {
        Map valueMap = [
                type: valueType,
                value: [
                        amount: (value as Money)?.amount,
                        currency: (value as Money)?.currency,
                        decimals: decimals,
                ]
        ]
        return Elements.encodeAsJSON(valueMap)
    }

}
