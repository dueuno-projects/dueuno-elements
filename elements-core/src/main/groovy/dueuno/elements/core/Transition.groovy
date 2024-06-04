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

import dueuno.elements.exceptions.ElementsException
import dueuno.elements.types.Types
import groovy.transform.CompileStatic
import org.grails.gsp.GroovyPagesTemplateEngine

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class Transition implements WebRequestAware {

    private List<TransitionCommand> commands
    private String view

    Transition() {
        view = '/dueuno/elements/core/Transition'
        commands = []
    }

    String getView() {
        return view
    }

    Map getModel() {
        return [c: this]
    }

    void clear() {
        commands.clear()
    }

    void redirect(Map args) {
        initializeWithRequestData(args)
        addCommand(
                TransitionCommandMethod.REDIRECT,
                null,
                null,
                new ComponentEvent(args).asMap(),
        )
    }

    void initializeWithRequestData(Map componentEventData) {
        if (!hasRequest() && !componentEventData.controller) {
            throw new ElementsException("The transition is outside a web request, a controller name must be specified (Eg. 't.redirect(controller: 'myController')')")
        }

        if (!hasRequest()) {
            return
        }

        if (componentEventData.action && !componentEventData.controller) {
            componentEventData.controller = getControllerName()
        }

        if (!componentEventData.action && componentEventData.controller) {
            componentEventData.action = componentEventData.action ?: 'index'
        }
    }

    void renderContent(PageContent content) {
        // If you change this string you need to change also the
        // file '_PageContent.gsp'
        String view = '''
            <div id="page-content"
                 class="page-content"
                 data-21-component="PageContent"
                 data-21-properties="${c.propertiesAsJSON}"
                 data-21-events="${c.eventsAsJSON}"
            >
                <page:colors component="${c}"/>            
                <render:componentList instance="${c}"/>
            </div>
        '''
        Map model = content.model

        StringWriter template = new StringWriter()
        gspEngine
                .createTemplate(view, 'pageContent')
                .make(model)
                .writeTo(template)

        addCommand(
                TransitionCommandMethod.CONTENT,
                null,
                null,
                template.toString(),
        )
    }

    void replace(String componentName, Component component, String templateRoot) {
        File view = new File(templateRoot + component.viewPath + '_' + component.viewTemplate + '.gsp')
        Map model = component.model

        StringWriter template = new StringWriter()
        gspEngine
                .createTemplate(view)
                .make(model)
                .writeTo(template)

        addCommand(
                TransitionCommandMethod.REPLACE,
                componentName,
                null,
                template.toString(),
        )
    }

    void append(String afterComponent, String newComponent) {
        addCommand(
                TransitionCommandMethod.APPEND,
                afterComponent,
                null,
                newComponent,
        )
    }

    void call(String component, String method, Map args = [:]) {
        addCommand(
                TransitionCommandMethod.CALL,
                component,
                method,
                args,
        )
    }

    void trigger(String component, String event) {
        addCommand(
                TransitionCommandMethod.TRIGGER,
                component,
                event,
                null
        )
    }

    void set(String component, Object value, Boolean trigger = true) {
        set(component, 'value', value, [], trigger)
    }

    void set(String component, String property, Object value, List valueArgs = [], Boolean trigger = true) {
        if (property == 'value') {
            if (value in Enum) value = value.toString()
        }

        if (value in String && hasRequest()) {
            value = message(value as String, valueArgs as Object[])
        }

        addCommand(
                TransitionCommandMethod.SET,
                component,
                property,
                value,
                trigger
        )
    }

    void infoMessage(String msg, Map onClick = null) {
        infoMessage('info', msg, [], onClick)
    }

    void infoMessage(String msg, List msgArgs, Map onCLick = null) {
        infoMessage('info', msg, msgArgs, onCLick)
    }

    void errorMessage(String msg, Map onCLick = null) {
        infoMessage('error', msg, [], onCLick)
    }

    void errorMessage(String msg, List msgArgs, Map onCLick = null) {
        infoMessage('error', msg, msgArgs, onCLick)
    }

    void infoMessage(String type, String msg, List msgArgs = [], Map onClick = null) {
        String infoMessage = hasRequest()
                ? message(msg, msgArgs as Object[])
                : msg

        if (onClick) {
            initializeWithRequestData(onClick)
        }

        Map args = [
                infoMessage: infoMessage,
                click      : onClick,
        ]
        call('messagebox', type, args)
    }

    void confirmMessage(String msg, Map onClick) {
        optionsMessage(msg, [], null, onClick)
    }

    void confirmMessage(String msg, List msgArgs, Map onClick) {
        optionsMessage(msg, msgArgs, null, onClick)
    }

    void optionsMessage(String msg, Map onOption1Click, Map onOption2Click) {
        optionsMessage(msg, [], onOption1Click, onOption2Click)
    }

    void optionsMessage(String msg, List msgArgs, Map onOption1Click, Map onOption2Click) {
        String confirmMessage = hasRequest()
                ? message(msg, msgArgs as Object[])
                : msg

        if (onOption1Click) initializeWithRequestData(onOption1Click)
        if (onOption2Click) initializeWithRequestData(onOption2Click)

        Map args = [
                confirmMessage: confirmMessage,
                option1Click  : onOption1Click,
                option2Click  : onOption2Click,
        ]
        call('messagebox', 'confirm', args)
    }

    GroovyPagesTemplateEngine getGspEngine() {
        return Elements.getBean('groovyPagesTemplateEngine') as GroovyPagesTemplateEngine
    }

    private void addCommand(TransitionCommandMethod method, String component, String property, Object value, Boolean trigger = true) {
        TransitionCommand command = new TransitionCommand()
        command.method = method as String
        command.component = component
        command.property = property
        command.value = Types.serializeValue(value)
        command.trigger = trigger
        commands.add(command)
    }

    String getTransitionAsJSON() {
        return Elements.encodeAsJSON(
                commands: commands,
        )
    }
}
