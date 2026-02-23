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
import dueuno.core.WebRequestAware
import dueuno.exceptions.ArgsException
import dueuno.exceptions.ElementsException
import dueuno.types.Types
import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class Transition implements WebRequestAware {

    private String view
    private List<TransitionCommand> commands
    private List<Component> components

    Transition() {
        view = '/dueuno/elements/Transition'
        commands = []
        components = []
    }

    String getView() {
        return view
    }

    Map getModel() {
        return [c: this]
    }

    void clear() {
        commands.clear()
        components.clear()
    }

    public <T> T addComponent(T component) {
        components.add(component as Component)
        return component
    }

    public <T> T addComponent(Class<T> clazz, String id = null, Map args = [:]) {
        args['class'] = clazz
        args['id'] = id ?: clazz.simpleName.toLowerCase()
        return addComponent(args)
    }

    public <T> T addComponent(Map args) {
        Class<T> clazz = ArgsException.requireArgument(args, 'class') as Class<T>
        String id = ArgsException.requireArgument(args, 'id')

        args.remove('class')
        args.remove('id')
        T component = Component.createInstance(clazz, id, args)
        addComponent(component as Component)

        return component
    }

    void redirect(Map args) {
        ComponentEvent event = new ComponentEvent(args)
        initializeWithRequestData(event)
        addCommand(
                TransitionCommandMethod.REDIRECT,
                null,
                null,
                event.asMap(),
        )
    }

    void initializeWithRequestData(LinkDefinition componentEventData) {
        if (!componentEventData) {
            return
        }

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
        addComponent(content)
        addCommand(
                TransitionCommandMethod.CONTENT,
                null,
                null,
                null,
        )
    }

    void remove(String component) {
        addCommand(
                TransitionCommandMethod.REMOVE,
                component,
                null,
                null,
        )
    }

    void replace(String component, String newComponent) {
        addCommand(
                TransitionCommandMethod.REPLACE,
                component,
                null,
                newComponent,
        )
    }

    void append(String parentComponent, String newComponent) {
        addCommand(
                TransitionCommandMethod.APPEND,
                parentComponent,
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

    void loading(Boolean show) {
        addCommand(
                TransitionCommandMethod.LOADING,
                null,
                null,
                show
        )
    }

    void setValue(String component, Object value, Boolean trigger = true) {
        set(component, 'value', value, [], trigger)
    }

    void set(String component, String property, Object value, List valueArgs = [], Boolean trigger = true) {
        if (property == 'value') {
            if (value in Enum) value = value.toString()
        }

        if (value in String && hasRequest()) {
            value = message(value as String, valueArgs)
        }

        addCommand(
                TransitionCommandMethod.SET,
                component,
                property,
                value,
                trigger
        )
    }

    void infoMessage(String msg, ComponentEvent onClick = null) {
        infoMessage('info', msg, null, onClick)
    }

    void infoMessage(String msg, List msgArgs, ComponentEvent onCLick = null) {
        infoMessage('info', msg, msgArgs, onCLick)
    }

    void errorMessage(String msg, ComponentEvent onCLick = null) {
        infoMessage('error', msg, null, onCLick)
    }

    void errorMessage(String msg, List msgArgs, ComponentEvent onCLick = null) {
        infoMessage('error', msg, msgArgs, onCLick)
    }

    void infoMessage(String type, String msg, List msgArgs = [], ComponentEvent onClick = null) {
        String infoMessage = hasRequest()
                ? message(msg, msgArgs)
                : msg

        Map args = [:]
        args.infoMessage = infoMessage

        if (onClick && (onClick.controller || onClick.action || onClick.url)) {
            initializeWithRequestData(onClick)
            args.click = onClick.asMap()
        }

        call('messagebox', type, args)
    }

    void confirmMessage(String msg, ComponentEvent onClickConfirm) {
        optionsMessage(msg, [], null, onClickConfirm)
    }

    void confirmMessage(String msg, List msgArgs, ComponentEvent onClickConfirm) {
        optionsMessage(msg, msgArgs, null, onClickConfirm)
    }

    void optionsMessage(String msg, ComponentEvent onClickCancel, ComponentEvent onClickConfirm) {
        optionsMessage(msg, [], onClickCancel, onClickConfirm)
    }

    void optionsMessage(String msg, List msgArgs, ComponentEvent onClickCancel, ComponentEvent onClickConfirm) {
        String confirmMessage = hasRequest()
                ? message(msg, msgArgs)
                : msg

        Map args = [:]
        args.confirmMessage = confirmMessage

        if (onClickCancel && (onClickCancel.controller || onClickCancel.action || onClickCancel.url)) {
            initializeWithRequestData(onClickCancel)
            args.clickCancel = onClickCancel.asMap()
        }

        if (onClickConfirm && (onClickConfirm.controller || onClickConfirm.action || onClickConfirm.url)) {
            initializeWithRequestData(onClickConfirm)
            args.clickConfirm = onClickConfirm.asMap()
        }

        call('messagebox', 'confirm', args)
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

    String getCommandsAsJSON() {
        return Elements.encodeAsJSON(commands)
    }

    List<Component> getComponents() {
        return components
    }
}
