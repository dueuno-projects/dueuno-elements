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

    Boolean autocomplete

    Form(Map args) {
        super(args)

        // DEFAULTS
        //
        keyFields = []
        validate = args.validate as Class ?: args.constraints as Class ?: null

        autocomplete = (args.autocomplete == null) ? false : args.autocomplete
    }

    List<FormField> getComponents() {
        List<FormField> fields = []

        for (component in super.components) {
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

        // Set common args
        if (args.readonly == null) args.readonly = readonly
        if (!args.primaryTextColor) args.primaryTextColor = primaryTextColor
        if (!args.primaryBackgroundColor) args.primaryBackgroundColor = primaryBackgroundColor

        // Add control/component. We add them to the components to be able to address
        // them directly instead of passing through the FormField (Eg. form.controlName)
        Component component
        if (clazz in Control) {
            component = addControl(args)
            setFieldValue(component)

        } else {
            component = addComponent(args)
        }

        // Set field specific args
        if (args.helpMessage == null) args.helpMessage = ''
        if (args.label == null) args.label = buildLabel(id)
        if (args.cols == null) args.cols = 12

        args.remove('cssClass')
        args.remove('cssStyle')
        args.remove('events')
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

    void addKeyField(String id, Number value = null) {
        addKeyField(id, 'NUMBER', value)
    }

    void addKeyField(String id, String valueType, Object value = null) {
        if (valueType == 'TEXT' && value in Enum) value = value.toString()

        FormField field = addField(
                class: HiddenField,
                id: id,
                valueType: valueType,
                value: value,
        )
        keyFields += field
    }

    @Override
    void setReadonly(Boolean isReadonly) {
        super.readonly = isReadonly
        for (field in components) {
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
            Control control = controlEntry.value
            setFieldValue(control, obj)
        }
    }

    private void setFieldValue(Control control, Object obj = null) {
        Object value = ObjectUtils.getValue(obj, control.id)

        if (value == null && control.value == null && requestParams.containsKey(control.id)) {
            control.value = requestParams[control.id]

        } else if (value == null && control.value == null && control.defaultValue != null) {
            control.value = control.defaultValue

        } else if (value != null) {
            control.value = value
        }
    }

}

