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

import dueuno.elements.style.TextStyle
import dueuno.elements.types.Types
import groovy.transform.CompileStatic

/**
 * A Control is a kind of component that can hold a value of a specific type. Controls are the leaves in the UI
 * components tree. They represent the interaction points to the user. Eg. a control can be a TextField, a DateField
 * or a Button.
 *
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
abstract class Control extends Component {

    /** Value hold by the control */
    Object value

    /** Type of the value managed by the control */
    String valueType

    /** Default value to use if no value has been assigned */
    Object defaultValue

    /** Whether or not the value can be null */
    Boolean nullable

    /** Text patter to accept as value. Priority is: 'invalidChars' then 'validChars' then 'pattern' */
    String invalidChars
    String validChars
    String pattern

    /** Properties used by {@link PrettyPrinter PrettyPrinter} to render the value */
    PrettyPrinterProperties prettyPrinterProperties

    /** Text styles */
    List<TextStyle> textStyle

    Control(Map args) {
        super(args)

        viewPath = args.viewPath ?: '/dueuno/elements/controls/'

        valueType = args.valueType
        nullable = (args.nullable == null) ? true : args.nullable

        setInvalidChars(args.invalidChars as String)
        setValidChars(args.validChars as String)
        setTextStyle(args.textStyle)
        pattern = args.pattern ?: ''

        prettyPrinterProperties = new PrettyPrinterProperties(args)
        prettyPrinterProperties.locale = locale
        prettyPrinterProperties.renderTextPrefix = args.renderTextPrefix == null ? true : args.renderTextPrefix
        prettyPrinterProperties.textPrefix = args.textPrefix ?: controllerName
        prettyPrinterProperties.textArgs = args.textArgs as List
        prettyPrinterProperties.prettyPrinter = args.prettyPrinter
        prettyPrinterProperties.transformer = args.transformer

        setDefaultValue(args.defaultValue)
        setValue(args.value)
    }

    Component onSubmit(Map args) {
        args.event = 'enter'
        return on(args)
    }

    void setTransformer(String value) {
        prettyPrinterProperties.transformer = value
    }

    String getTransformer() {
        return prettyPrinterProperties.transformer
    }

    void setPrettyPrinter(String value) {
        prettyPrinterProperties.prettyPrinter = value
    }

    String getPrettyPrinter() {
        return prettyPrinterProperties.prettyPrinter
    }

    void setDefaultValue(Object value) {
        defaultValue = value
    }

    void setTextStyle(Object value) {
        switch (value) {
            case TextStyle:
                textStyle = [value as TextStyle]
                break

            case List<TextStyle>:
                textStyle = value as List<TextStyle>
                break

            default:
                textStyle = [TextStyle.BOLD]
        }
    }

    String getTextStyle() {
        return textStyle.join(' ')
    }

    /**
     * Returns the value as rendered by the {@link PrettyPrinter PrettyPrinter}
     * @return The pretty printed value
     */
    String getPrettyValue() {
        return prettyPrint(value, prettyPrinterProperties)
    }

    void setInvalidChars(String chars) {
        invalidChars = chars ? '^[^' + escapeSpecialChars(chars) + ']*$' : null
    }

    void setValidChars(String chars) {
        validChars = chars ? '^[' + escapeSpecialChars(chars) + ']*$' : null
    }

    private String escapeSpecialChars(String chars) {
        String result = ''

        for (String c in chars) {
            if ('.^$*+-?()[]{}\\|'.contains(c)) {
                result = result + '\\' + c
            } else {
                result += c
            }
        }

        return result
    }

    String getValueAsJSON() {
        if (!valueType) {
            return Elements.encodeAsJSON([:])
        }

        Map valueMap = Types.serializeValue(value, valueType)
        return Elements.encodeAsJSON(valueMap)
    }

    @Override
    String getPropertiesAsJSON(Map properties = [:]) {
        Map thisProperties = [
                nullable: nullable,
                pattern: invalidChars ?: validChars ?: pattern,
        ]
        return super.getPropertiesAsJSON(thisProperties + properties)
    }

}