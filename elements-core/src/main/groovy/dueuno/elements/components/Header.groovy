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
import dueuno.elements.style.TextDefault
import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 */

@CompileStatic
class Header extends Component {

    private Boolean isSticky

    String title
    List titleArgs
    String icon

    Boolean hasNextButton
    Boolean hasBackButton

    Button nextButton
    Button backButton

    Header(Map args) {
        super(args)

        icon = args.icon
        String defaultTitle = controllerName + '.' + actionName + '.header.title'
        title = (args.title == null) ? defaultTitle : args.title
        titleArgs = (args.titleArgs == null) ? [] : args.titleArgs as List

        hasBackButton = false
        hasNextButton = false

        backButton = createControl(
                class: Button,
                id: 'backButton',
        )
        nextButton = createControl(
                class: Button,
                id: 'nextButton',
                primary: true,
        )
    }

    Button addNextButton(Map args = [:]) {
        args.controller = args.controller ?: controllerName
        args.action = args.action ?: 'onConfirm'
        args.text = (args.text == null) ? TextDefault.SAVE: args.text
        args.icon = (args.icon == null) ? 'fa-solid fa-check' : args.icon
        if (args.group) nextButton.group = args.group

        nextButton.removeDefaultAction()
        nextButton.addDefaultAction(args)
        hasNextButton = true
        if (sticky == null) isSticky = true

        return nextButton
    }

    Button removeNextButton() {
        if (sticky == null && !hasBackButton) isSticky = false
        nextButton.removeDefaultAction()
        hasNextButton = false
        return nextButton
    }

    Button addBackButton(Map args = [:]) {
        if (!args.controller && !args.action && hasReturnPoint()) {
            args.controller = args.controller ?: returnPointController ?: controllerName
            args.action = args.action ?: returnPointAction ?: 'index'
            args.params = (args.params in Map ? args.params as Map : [:]) + returnPointParams

        } else {
            args.controller = args.controller ?: controllerName
            args.action = args.action ?: 'index'
            args.params = args.params ?: [:]
        }

        args.text = (args.text == null) ? TextDefault.BACK: args.text
        args.icon = (args.icon == null) ? 'fa-angle-left' : args.icon
        if (args.group) nextButton.group = args.group

        backButton.removeDefaultAction()
        backButton.addDefaultAction(args)
        hasBackButton = true
        if (sticky == null) isSticky = true

        return backButton
    }

    Button removeBackButton() {
        if (sticky == null && !hasNextButton) isSticky = false
        backButton.removeDefaultAction()
        hasBackButton = false
        return backButton
    }

    void setTitle(Object value) {
        title = prettyPrint(value)
    }

    @Override
    String getPropertiesAsJSON(Map properties = [:]) {
        Map thisProperties = [
                sticky: sticky == null ? isSticky : sticky,
        ]
        return super.getPropertiesAsJSON(thisProperties + properties)
    }

}
