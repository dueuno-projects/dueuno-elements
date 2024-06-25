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
package dueuno.elements.components

import dueuno.commons.utils.ObjectUtils
import dueuno.elements.controls.HiddenField
import dueuno.elements.core.Component
import dueuno.elements.core.Control
import dueuno.elements.core.Elements
import dueuno.elements.exceptions.ArgsException
import dueuno.elements.exceptions.ElementsException
import grails.gorm.validation.ConstrainedProperty
import grails.validation.Validateable
import groovy.transform.CompileStatic

import java.lang.reflect.Field

/**
 * @author Gianluca Sartori
 */

@CompileStatic
class Form extends Component {

    List keyFields
    Class validate

    Boolean readonly
    Boolean autocomplete

    Form(Map args) {
        super(args)

        // DEFAULTS
        //
        keyFields = []
        validate = args.validate as Class ?: args.constraints as Class ?: null

        readonly = (args.readonly == null) ? false : args.readonly
        autocomplete = (args.autocomplete == null) ? false : args.autocomplete
    }

    List<FormField> getFields() {
        List<FormField> fields = []

        for (component in components) {
            if (component in FormField) {
                fields.add(component as FormField)
            }
        }

        return fields
    }

    FormField addField(Map args) {
        Class clazz = ArgsException.requireArgument(args, 'class') as Class
        String id = ArgsException.requireArgument(args, 'id')

        if (validate) {
            Map fieldConstraints = getFieldConstraints(validate, id)

            // Auto assigns 'nullable' flag
            if (args.nullable == null) {
                args.nullable = fieldConstraints.nullable
            }

            // Auto assign 'maxSize'
            if (args.maxSize == null) {
                args.maxSize = fieldConstraints.maxSize ?: 255 // GORM default value for strings
            }
        }

        // Auto assigns values from 'params'
        if (args.value == null && requestParams[id]) {
            args.value = requestParams[id]
        }

        // Add control/component. We add them to the components to be able to address
        // them directly instead of passing through the FormField (Eg. form.controlName)
        Component component
        if (clazz in Control) {
            component = addControl(args)
        } else {
            component = addComponent(args)
        }

        // Add field
        if (args.helpMessage == null) args.helpMessage = ''
        if (args.label == null) args.label = buildLabel(id)
        if (args.cols == null) args.cols = 12
        if (args.readonly == null) args.readonly = readonly
        if (!args.primaryTextColor) args.primaryTextColor = primaryTextColor
        if (!args.primaryBackgroundColor) args.primaryBackgroundColor = primaryBackgroundColor

        args.cssClass = null
        args.cssStyle = null
        args.events = null
        args.component = component
        args.putAll(component.containerSpecs)

        FormField field = addComponent(FormField, id + 'Field', args)
        return field
    }

    private Map getFieldConstraints(Class domainOrCommandClass, String fieldName) {
        if (!domainOrCommandClass || !fieldName)
            return [:]

        List<String> fieldParts = fieldName.split('\\.') as List<String>
        Field field = domainOrCommandClass.declaredFields.find { it.name == fieldParts[0] }
        if (!field) {
            return [:]
        }

        Boolean isDomainClass = Elements.isDomainClass(domainOrCommandClass)
        Boolean isCommandClass = Validateable.isAssignableFrom(domainOrCommandClass)
        if (!isDomainClass && !isCommandClass) {
            throw new ElementsException(
                    "Cannot retrieve constraints from class '${domainOrCommandClass}', " +
                            "please specify a GORM Domain class or a class that implements '${Validateable.name}'")
        }

        Map<String, ConstrainedProperty> constraintsMap = isDomainClass
                ? domainOrCommandClass['constrainedProperties'] as Map<String, ConstrainedProperty>
                : domainOrCommandClass['constraintsMap'] as Map<String, ConstrainedProperty>

        Boolean isSimpleField = (fieldParts.size() == 1)
        if (isSimpleField) {
            Map result = [:]
            ConstrainedProperty fieldConstraints = constraintsMap[fieldName]
            if (fieldConstraints) {
                result.nullable = fieldConstraints['nullable']
                result.maxSize = fieldConstraints['maxSize']
            }
            return result

        } else { // move to the next field in path
            Class nextFieldClass = field.getType()
            String nextFieldName = fieldParts.tail().join('.')
            return getFieldConstraints(nextFieldClass, nextFieldName)
        }
    }

    void addKeyField(String name, Number value = null) {
        addKeyField(name, 'NUMBER', value)
    }

    void addKeyField(String name, String valueType, Object value = null) {
        if (valueType == 'TEXT' && value in Enum) value = value.toString()

        FormField field = addField(
                class: HiddenField,
                id: name,
                valueType: valueType,
                value: value,
        )
        keyFields += field
    }

    void setReadonly(Boolean isReadonly) {
        readonly = isReadonly
        for (field in fields) {
            (field as FormField).component.readonly = isReadonly
        }
    }

    void setValues(Object obj) {
        if (Elements.hasId(obj) && !getControl('id')) {
            // This is not sufficient, we must add the fields that represents the real GORM key
            // that could be a composite key
            addKeyField('id')
        }

        for (controlEntry in controls) {
            String controlName = controlEntry.key
            Control control = controlEntry.value

            if (control.value == null) {
                control.value = ObjectUtils.getValue(obj, controlName)
            }
        }
    }
}

