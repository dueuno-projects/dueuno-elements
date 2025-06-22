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
import dueuno.elements.core.PrettyPrinterProperties
import dueuno.elements.exceptions.ElementsException
import dueuno.elements.types.Quantity
import dueuno.elements.types.QuantityUnit
import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class QuantityField extends NumberField {

    Map<String, String> unitOptions
    QuantityUnit defaultUnit

    QuantityField(Map args) {
        super(args)

        viewTemplate = 'QuantityField'
        valueType = Quantity.TYPE_NAME

        decimals = args.decimals == null ? 2 : args.decimals as Integer
        negative = (args.negative == null) ? false : args.negative
        unitOptions = unitListToOptions(args.availableUnits as List)
        setDefaultUnit(args.defaultUnit as QuantityUnit)

        inputMode = decimals ? TextFieldInputMode.DECIMAL : TextFieldInputMode.NUMERIC
    }

    void setDefaultUnit(QuantityUnit value) {
        if (unitOptions && !value) {
            defaultUnit = unitOptions.keySet()[0]

        } else if (value) {
            defaultUnit = value

        } else {
            defaultUnit = QuantityUnit.ND
        }
    }

    Map<String, String> unitListToOptions(List list) {
        if (!list) {
            return [:]
        }

        Map<String, String> results = [:]
        for (value in list) {
            // We only need to translate the Unit, not to transform it or do other stuff with it
            PrettyPrinterProperties renderProperties = new PrettyPrinterProperties()
            renderProperties.locale = prettyPrinterProperties.locale
            results[(value as String)] = prettyPrint(value as QuantityUnit, renderProperties)
        }
        return results
    }

    String getPrettyDefaultUnit() {
        // We only need to translate the Unit, not to transform it or do other stuff with it
        PrettyPrinterProperties renderProperties = new PrettyPrinterProperties()
        renderProperties.locale = prettyPrinterProperties.locale
        return prettyPrint(defaultUnit, renderProperties)
    }

    @Override
    void setValue(Object value) {
        super.setValue(value)

        if (this.value != null && (this.value !instanceof Quantity)) {
            throw new ElementsException("${this.getClass().simpleName} can only accept values of type '${Quantity.name}'")
        }

        if (this.value) {
            setDefaultUnit((this.value as Quantity).unit)
        }
    }

    @Override
    String getValueAsJSON() {
        Map valueMap = [
                type: valueType,
                value: [
                        amount: (value as Quantity)?.amount,
                        unit: (value as Quantity)?.unit as String,
                        decimals: decimals,
                ]
        ]

        return Elements.encodeAsJSON(valueMap)
    }
}
