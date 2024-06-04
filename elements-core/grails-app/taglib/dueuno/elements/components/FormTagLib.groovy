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

import dueuno.elements.core.Component
import dueuno.elements.core.Control

/**
 * @author Gianluca Sartori
 */
class FormTagLib {

    static namespace = "form"

    def renderFields = { attrs ->
        def fields = attrs.instance.fields

        for (field in fields) {
            setFieldValue(field)
            out << render(template: field.view, model: field.model)
        }
    }

    private setFieldValue(Component field) {
        def control = field.component
        if (control !in Control) {
            return
        }

        if (control.value == null && control.defaultValue != null) {
            control.value = control.defaultValue
        }
    }
}
