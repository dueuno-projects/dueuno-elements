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

import dueuno.elements.controls.MoneyField
import dueuno.elements.core.PrettyPrinter
import dueuno.elements.core.PrettyPrinterProperties
import dueuno.elements.exceptions.ArgsException
import grails.gorm.MultiTenant
import grails.gorm.annotation.Entity
import groovy.transform.CompileDynamic
import org.grails.datastore.gorm.GormEntity

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@Entity
@CompileDynamic
class Money extends Number implements CustomType, GormEntity, MultiTenant<Money> {

    static final TYPE_NAME = 'MONEY'
    static final TYPE_FIELD = MoneyField
    static final TYPE_VALUE_PROPERTY_TYPE = Number
    static final TYPE_VALUE_PROPERTY_NAME = 'amount'

    BigDecimal amount
    String currency

    static constraints = {
        amount scale: 6
        currency maxSize: 3
    }

    Money() {
        this(0)
    }

    Money(Double amount, String currency = 'EUR') {
        this(new BigDecimal(amount), currency)
    }

    Money(BigDecimal amount, String currency = 'EUR') {
        this.amount = amount
        this.currency = currency
    }

    static String getCustomTypeName() {
        return TYPE_NAME
    }

    static Class getCustomTypeField() {
        return MoneyField
    }

    Map serialize() {
        return [
                type : TYPE_NAME,
                value: [
                        amount: amount,
                        currency: currency,
                ]
        ]
    }

    void deserialize(Map valueMap) {
        Map value = valueMap.value as Map
        if (!value) {
            return
        }

        currency = value.currency ?: 'EUR'
        amount = Types.deserializeBigDecimal(
                value.amount as String,
                value.decimals as Integer
        )
    }

    String prettyPrint(PrettyPrinterProperties properties) {
        if (amount == null)
            return ''

        String currency = printCurrency(properties)
        String amount = PrettyPrinter.printDecimal(amount, properties)

        Boolean prefixedUnit = properties.prefixedUnit == null ? false : properties.prefixedUnit
        return prefixedUnit
                ? currency + ' ' + amount
                : amount + ' ' + currency
    }

    String printCurrency(PrettyPrinterProperties properties) {
        if (currency == null)
            return ''

        Boolean symbolicCurrency = properties.symbolicCurrency == null ? true : properties.symbolicCurrency
        if (symbolicCurrency) {
            PrettyPrinterProperties renderProperties = new PrettyPrinterProperties()
            renderProperties.locale = properties.locale
            renderProperties.textPrefix = 'money.currency'
            return PrettyPrinter.printString(currency.toString(), renderProperties)

        } else {
            return currency.toString()
        }
    }

    String toString() {
        return amount
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


    Boolean equals(Money money) {
        return (amount == money.amount && currency == money.currency)
    }

    private void checkOperandsCompatibility(Money money) {
        if (currency != money.currency)
            throw new ArgsException("Cannot operate on money with different currencies: $currency + $money.currency")
    }

    Money plus(Money money) {
        checkOperandsCompatibility(money)
        return new Money(this.amount + money.amount, this.currency)
    }

    Money minus(Money money) {
        checkOperandsCompatibility(money)
        return new Money(this.amount - money.amount, this.currency)
    }

    Money multiply(Money money) {
        checkOperandsCompatibility(money)
        return new Money(this.amount * money.amount, this.currency)
    }

    Money div(Money money) {
        checkOperandsCompatibility(money)
        return new Money(this.amount / money.amount, this.currency)
    }


    //
    // Number
    //
    Boolean equals(Number amount) {
        return (this.amount == amount)
    }

    Money plus(Number amount) {
        return new Money(this.amount + amount, this.currency)
    }

    Money minus(Number amount) {
        return new Money(this.amount - amount, this.currency)
    }

    Money multiply(Number amount) {
        return new Money(this.amount * amount, this.currency)
    }

    Money div(Number amount) {
        return new Money(this.amount / amount, this.currency)
    }


    //
    // Quantity
    //
    Money multiply(Quantity quantity) {
        return new Money(this.amount * quantity.amount, this.currency)
    }
}