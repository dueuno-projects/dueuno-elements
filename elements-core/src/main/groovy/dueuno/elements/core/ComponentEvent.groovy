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

import dueuno.elements.types.Types
import groovy.transform.CompileStatic

/**
 * Components and Controls can trigger events. Actions can be registered to events on any component that will trigger it
 * depending on its logic. Please refer to each component documentation to know what events can be registered.
 *
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class ComponentEvent extends LinkDefinition {

    /** Name of the event */
    String on

    ComponentEvent(Map args) {
        super(args)
        on = args.on ?: 'click'
    }

    Map asMap() {
        return [
                namespace: namespace,
                controller: controller,
                action: action,
                url: url,
                params: Types.serialize(params),
                submit: submit,
                direct: direct,
                target: target,
                loading: loading,
                infoMessage: infoMessage,
                confirmMessage: confirmMessage,
                renderProperties: renderProperties.asMap(),
        ]
    }
}
