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
package dueuno.elements

import dueuno.core.LinkDefinition
import dueuno.elements.controls.HiddenField
import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 */

@CompileStatic
class KeyPress extends Component {

    LinkDefinition linkDefinition
    HiddenField buffer

    Boolean enabled
    String triggerKey
    Integer readingSpeed
    Integer bufferTimeout
    Boolean hideInput

    KeyPress(Map args) {
        super(args)

        viewPath = '/dueuno/elements/'

        enabled = args.enabled == null ? true : args.enabled
        triggerKey = args.triggerKey ?: 'Enter'

        // a barcode reader is typically much faster at typing than a human...
        // let's use this principle to understand if the typing comes from a reader (ms)
        // (only evaluated if focus is on an "input" element)
        readingSpeed = args.readingSpeed == null ? 20 : args.readingSpeed as Integer

        // to prevent accidental typing, the buffer empties after a certain time (ms)
        bufferTimeout = args.bufferTimeout == null ? 500 : args.bufferTimeout as Integer

        // avoid writing text if focus is on an "input" element (default false)
        hideInput = args.hideInput == null ? false : args.hideInput

        linkDefinition = new LinkDefinition(args)
        linkDefinition.controller = args.controller ?: 'keyPress'
        linkDefinition.action = args.action ?: 'onKeyPress'

        buffer = createControl(
                class: HiddenField,
                id: 'buffer',
        )

        setOnKeyPressEvent()
    }

    String getKeyPressed() {
        return getGrailsWebRequest().params._21KeyPressed
    }

    private void setOnKeyPressEvent() {
        on(linkDefinition.properties + [
                event: 'keypress',
        ])
    }

    @Override
    String getPropertiesAsJSON() {
        Map thisProperties = [
                enabled: enabled,
                triggerKey: triggerKey,
                readingSpeed: readingSpeed,
                bufferTimeout: bufferTimeout,
                hideInput: hideInput,
        ]
        return Elements.encodeAsJSON(thisProperties)
    }

    String getNamespace() {
        return linkDefinition.namespace
    }

    void setNamespace(String value) {
        linkDefinition.namespace = value
        setOnKeyPressEvent()
    }

    String getController() {
        return linkDefinition.controller
    }

    void setController(String value) {
        linkDefinition.controller = value
        setOnKeyPressEvent()
    }

    String getAction() {
        return linkDefinition.action
    }

    void setAction(String value) {
        linkDefinition.action = value
        setOnKeyPressEvent()
    }

    Map getParams() {
        return linkDefinition.params
    }

    void setParams(Map value) {
        linkDefinition.params = value
        setOnKeyPressEvent()
    }

    List<String> getSubmit() {
        return linkDefinition.submit
    }

    void setSubmit(List<String> value) {
        linkDefinition.submit = value
        setOnKeyPressEvent()
    }
}
