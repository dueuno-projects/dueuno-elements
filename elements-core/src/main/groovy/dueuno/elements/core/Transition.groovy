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

import dueuno.elements.exceptions.ArgsException
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

    private String view
    private List<TransitionCommand> commands
    private List<Component> components

    Transition() {
        view = '/dueuno/elements/core/Transition'
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
        initializeWithRequestData(args)
        addCommand(
                TransitionCommandMethod.REDIRECT,
                null,
                null,
                new ComponentEvent(args).asMap(),
        )
    }

    void initializeWithRequestData(Map componentEventData) {
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

    void replace(String component, String newComponent) {
        addCommand(
                TransitionCommandMethod.REPLACE,
                component,
                null,
                newComponent,
        )
    }

    void append(String component, String newComponent) {
        addCommand(
                TransitionCommandMethod.APPEND,
                component,
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

    void infoMessage(String msg, Map onClick = [:]) {
        infoMessage('info', msg, [], onClick)
    }

    void infoMessage(String msg, List msgArgs, Map onCLick = [:]) {
        infoMessage('info', msg, msgArgs, onCLick)
    }

    void errorMessage(String msg, Map onCLick = [:]) {
        infoMessage('error', msg, [], onCLick)
    }

    void errorMessage(String msg, List msgArgs, Map onCLick = [:]) {
        infoMessage('error', msg, msgArgs, onCLick)
    }

    void infoMessage(String type, String msg, List msgArgs = [], Map onClick = [:]) {
        String infoMessage = hasRequest()
                ? message(msg, msgArgs as Object[])
                : msg

        Map args = [:]
        args.infoMessage = infoMessage

        if (onClick.controller || onClick.action || onClick.url) {
            initializeWithRequestData(onClick)
            args.click = new ComponentEvent(onClick).asMap()
        }

        call('messagebox', type, args)
    }

    void confirmMessage(String msg, Map onClick) {
        optionsMessage(msg, [], [:], onClick)
    }

    void confirmMessage(String msg, List msgArgs, Map onClick) {
        optionsMessage(msg, msgArgs, [:], onClick)
    }

    void optionsMessage(String msg, Map onOption1Click, Map onOption2Click) {
        optionsMessage(msg, [], onOption1Click, onOption2Click)
    }

    void optionsMessage(String msg, List msgArgs, Map onOption1Click, Map onOption2Click) {
        String confirmMessage = hasRequest()
                ? message(msg, msgArgs as Object[])
                : msg

        Map args = [:]
        args.confirmMessage = confirmMessage

        if (onOption1Click.controller || onOption1Click.action || onOption1Click.url) {
            initializeWithRequestData(onOption1Click)
            args.option1Click = new ComponentEvent(onOption1Click).asMap()
        }

        if (onOption2Click.controller || onOption2Click.action || onOption2Click.url) {
            initializeWithRequestData(onOption2Click)
            args.option2Click = new ComponentEvent(onOption2Click).asMap()
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
