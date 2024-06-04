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

import dueuno.elements.core.PrettyPrinter
import dueuno.elements.core.PrettyPrinterProperties
import dueuno.elements.exceptions.ArgsException
import grails.gorm.MultiTenant
import grails.gorm.annotation.Entity
import groovy.transform.CompileDynamic

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 * @author Alessandro Stecca
 */

@Entity
@CompileDynamic
class Quantity extends Number implements CustomType, MultiTenant<Quantity> {

    BigDecimal amount
    QuantityUnit unit

    static constraints = {
        amount scale: 6
        unit maxSize: 3
    }

    Quantity() {
        this(0)
    }

    Quantity(Double amount, QuantityUnit unit = QuantityUnit.PCS) {
        this(new BigDecimal(amount), unit)
    }

    Quantity(BigDecimal amount, QuantityUnit unit = QuantityUnit.PCS) {
        this.amount = amount
        this.unit = unit
    }

    Map serialize() {
        return [
                type : 'QUANTITY',
                value: [
                        amount: amount,
                        unit: unit as String,
                ]
        ]
    }

    void deserialize(Map valueMap) {
        Map value = valueMap.value as Map
        if (!value) {
            return
        }

        unit = value.unit ? (QuantityUnit) value.unit : QuantityUnit.NR
        amount = Types.deserializeBigDecimal(
                value.amount as String,
                value.decimals as Integer
        )
    }

    String prettyPrint(PrettyPrinterProperties properties) {
        if (amount == null)
            return ''

        String unit = printUnit(properties)
        String amount = PrettyPrinter.printDecimal(amount, properties)

        Boolean prefixedUnit = properties.prefixedUnit == null ? false : properties.prefixedUnit
        return prefixedUnit
                ? unit + ' ' + amount
                : amount + ' ' + unit
    }

    String toString() {
        return amount
    }

    String printUnit(PrettyPrinterProperties properties) {
        if (unit == null)
            return ''

        Boolean symbolicQuantity = properties.symbolicQuantity == null ? true : properties.symbolicQuantity
        if (symbolicQuantity) {
            PrettyPrinterProperties renderProperties = new PrettyPrinterProperties()
            renderProperties.locale = properties.locale
            renderProperties.messagePrefix = 'quantity.unit'
            return PrettyPrinter.printString(unit.toString(), renderProperties)

        } else {
            return unit.toString()
        }
    }

    int intValue() {
        return amount.intValue()
    }

    long longValue() {
        return amount.longValue()
    }

    float floatValue() {
        return amount.floatValue()
    }

    double doubleValue() {
        return amount.doubleValue()
    }

    //
    // Quantities
    //
    Quantity plus(Quantity q) {
        return new Quantity((this.amount + q.convert(this.unit).amount), this.unit)
    }

    Quantity minus(Quantity q) {
        return new Quantity((this.amount - q.convert(this.unit).amount), this.unit)
    }

//    Quantity multiply(Quantity q) {
//        return new Quantity((this.amount * q.convertForMultiply(this.unit).amount), getResultUnit(q))
//    }


    //
    // Numbers
    //
    Quantity plus(Number n) {
        return new Quantity(this.amount + n, this.unit)
    }

    Quantity minus(Number n) {
        return new Quantity(this.amount - n, this.unit)
    }

    Quantity multiply(Number n) {
        return new Quantity(this.amount * n, this.unit)
    }

    Quantity div(Number n) {
        return new Quantity(this.amount / n, this.unit)
    }


    //
    // Convertions
    //
    Quantity convert(QuantityUnit toUnit) {
        if (unit.parent != toUnit.parent) {
            throw new ArgsException("Cannot convert '${unit.parent}' to '${toUnit.parent}'")
        }

        if (toUnit in [QuantityUnit.MASS, QuantityUnit.LENGTH, QuantityUnit.AREA, QuantityUnit.VOLUME]) {
            Integer startMagnitude = unit.magnitude
            Integer resultMagnitude = toUnit.magnitude
            Integer diff = startMagnitude - resultMagnitude
            return new Quantity(this.amount * (10.power(diff)), toUnit)
        }

        if (toUnit in [QuantityUnit.TIME]) {

        }

        throw new ArgsException("Cannot convert '${unit.parent}' to '${toUnit.parent}'")
    }

    private Quantity fromSeconds(Double seconds, QuantityUnit unit) {
        if (unit.parent != QuantityUnit.TIME) {
            throw new ArgsException("Please specify one of the 'TIME' units")
        }

        switch (unit) {
            case QuantityUnit.MSEC: return new Quantity(seconds * 1000, QuantityUnit.MSEC)
            case QuantityUnit.SEC: return new Quantity(seconds, QuantityUnit.SEC)
            case QuantityUnit.MIN: return new Quantity(seconds * 60, QuantityUnit.MIN)
            case QuantityUnit.HOUR: return new Quantity(seconds * 60 * 60, QuantityUnit.HOUR)
        }

        throw new ArgsException("Cannot convert seconds to ${unit}")
    }

    private Double toSeconds(Quantity quantity) {
        if (quantity.unit.parent != QuantityUnit.TIME) {
            throw new ArgsException("Please specify a 'TIME' quantity")
        }

        switch (quantity.unit) {
            case QuantityUnit.MSEC: return quantity.amount / 1000
            case QuantityUnit.SEC: return quantity.amount
            case QuantityUnit.MIN: return quantity.amount * 60
            case QuantityUnit.HOUR: return quantity.amount * 60 * 60
        }

        throw new ArgsException("Cannot convert ${quantity.unit} to seconds")
    }

    private QuantityUnit getResultUnit(Quantity quantity) {
        if (unit.parent == quantity.unit.parent) {
            return getUpperUnit(quantity)
        } else if (unit.parent == 'LENGTH' && quantity.unit.parent == 'SQUARE') {
            return (getUpperUnit(getUpperUnit(this)))
        } else if (unit.parent == 'SQUARE' && quantity.unit.parent == 'LENGTH') {
            return (getUpperUnit(this))
        } else if (unit.parent == 'VOLUME' && quantity.unit.parent == 'LENGTH') {
            return unit
        } else if (unit.parent == 'LENGTH' && quantity.unit.parent == 'VOLUME') {
            return (getUpperUnit(getUpperUnit(this)))
        } else {
            throw new ArgsException("Cannot multiply '${unit}' with '${quantity.unit}'")
        }
    }

    private Quantity convertForMultiply(QuantityUnit toUnit) {
        if (unit.parent == toUnit.parent) {
            return convert(toUnit)
        } else if (unit.parent == 'LENGTH' && toUnit.parent == 'SQUARE') {
            return convert(getLowerUnit(toUnit))
        } else if (unit.parent == 'SQUARE' && toUnit.parent == 'LENGTH') {
            return convert(getUpperUnit(toUnit))
        } else if (unit.parent == 'VOLUME' && toUnit.parent == 'LENGTH') {
            return convert(getUpperUnit(getUpperUnit(toUnit)))
        } else if (unit.parent == 'LENGTH' && toUnit.parent == 'VOLUME') {
            return convert(getLowerUnit(getLowerUnit(toUnit)))
        } else {
            throw new ArgsException("Cannot multiply '${unit}' with '${toUnit}'")
        }
    }

    private QuantityUnit getUpperUnit(Quantity quantity) {
        return quantity.getUpperUnit(quantity.unit)
    }

    private QuantityUnit getUpperUnit(QuantityUnit unit) {
        QuantityUnit result
        if (unit == QuantityUnit.KM) {
            result = QuantityUnit.KM2
        } else if (unit == QuantityUnit.HM) {
            result = QuantityUnit.HM2
        } else if (unit == QuantityUnit.DAM) {
            result = QuantityUnit.DAM2
        } else if (unit == QuantityUnit.M) {
            result = QuantityUnit.M2
        } else if (unit == QuantityUnit.DM) {
            result = QuantityUnit.DM2
        } else if (unit == QuantityUnit.CM) {
            result = QuantityUnit.CM2
        } else if (unit == QuantityUnit.UM) {
            result = QuantityUnit.UM2
        } else if (unit == QuantityUnit.KM2) {
            result = QuantityUnit.KM3
        } else if (unit == QuantityUnit.HM2) {
            result = QuantityUnit.HM3
        } else if (unit == QuantityUnit.DAM2) {
            result = QuantityUnit.DAM3
        } else if (unit == QuantityUnit.M2) {
            result = QuantityUnit.M3
        } else if (unit == QuantityUnit.DM2) {
            result = QuantityUnit.DM3
        } else if (unit == QuantityUnit.CM2) {
            result = QuantityUnit.CM3
        } else if (unit == QuantityUnit.UM2) {
            result = QuantityUnit.UM3
        } else {
            throw new ArgsException("Cannot multiply '${this.unit}' with '${unit}'")
        }
        return result
    }

    private QuantityUnit getLowerUnit(QuantityUnit unit) {
        QuantityUnit result
        switch (unit) {
            case QuantityUnit.KM3:
                result = QuantityUnit.KM2
                break
            case QuantityUnit.HM3:
                result = QuantityUnit.HM2
                break
            case QuantityUnit.DAM3:
                result = QuantityUnit.DAM2
                break
            case QuantityUnit.M3:
                result = QuantityUnit.M2
                break
            case QuantityUnit.DM3:
                result = QuantityUnit.DM2
                break
            case QuantityUnit.CM3:
                result = QuantityUnit.CM2
                break
            case QuantityUnit.MM3:
                result = QuantityUnit.MM2
                break
            case QuantityUnit.UM3:
                result = QuantityUnit.UM2
                break
            case QuantityUnit.KM2:
                result = QuantityUnit.KM
                break
            case QuantityUnit.HM2:
                result = QuantityUnit.HM
                break
            case QuantityUnit.DAM2:
                result = QuantityUnit.DAM
                break
            case QuantityUnit.M2:
                result = QuantityUnit.M
                break
            case QuantityUnit.DM2:
                result = QuantityUnit.DM
                break
            case QuantityUnit.CM2:
                result = QuantityUnit.CM
                break
            case QuantityUnit.MM2:
                result = QuantityUnit.MM
                break
            case QuantityUnit.UM2:
                result = QuantityUnit.UM
                break
            default:
                //result = null
                throw new ArgsException("Cannot multiply '${this.unit}' with '${unit}'")
                break
        }
        return result
    }
}
