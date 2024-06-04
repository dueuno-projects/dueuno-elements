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

import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class EmailField extends TextField {

    EmailField(Map args) {
        super(args)

        viewTemplate = 'TextField'
        keyboardType = args.keyboardType as TextFieldKeyboardType ?: TextFieldKeyboardType.EMAIL
        pattern = args.pattern ?: '^(?!.*@.*@)(?!.*(\\.)\\1).[A-Za-z0-9_\\-\\.@]*$'
        icon = 'fa-envelope'
    }
}
