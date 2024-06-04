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
package dueuno.elements.pages

import dueuno.elements.components.Button
import dueuno.elements.components.Form
import dueuno.elements.components.Link
import dueuno.elements.controls.Checkbox
import dueuno.elements.controls.PasswordField
import dueuno.elements.controls.TextField
import dueuno.elements.core.Page
import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class Login extends Page {

    String copy
    Boolean register
    Boolean passwordRecovery
    Boolean autocomplete

    String logoImage
    String backgroundImage
    String backgroundCover
    String backgroundMargin

    Boolean displayFullscreen
    Boolean formOnRight
    Boolean loginEffect

    Form form

    Link registerLink
    Link passwordRecoveryLink

    Login(Map args) {
        super(args)

        copy = args.copy
        register = false
        passwordRecovery = false

        logoImage = ''
        backgroundImage = ''
        backgroundCover = ''
        backgroundMargin = ''

        displayFullscreen = false
        formOnRight = false
        loginEffect = true

        form = createComponent(Form)
        form.with {
            autocomplete = (args.autocomplete == null) ? false : args.autocomplete
            addField(
                    class: TextField,
                    id: 'username',
                    placeholder: 'springSecurity.login.username.label',
                    displayLabel: false,
            )
            addField(
                    class: PasswordField,
                    id: 'password',
                    icon: '',
                    placeholder: 'springSecurity.login.password.label',
                    displayLabel: false,
            )
            addField(
                    class: Checkbox,
                    id: 'rememberMe',
                    text: 'springSecurity.login.remember.me.label',
                    displayLabel: false,
            )
            addField(
                    class: Button,
                    id: 'login',
                    action: 'authenticate',
                    text: 'springSecurity.login.button',
                    submit: 'form',
                    stretch: true,
                    displayLabel: false,
                    primary: true,
            )
        }

        registerLink = createControl(
                class: Link,
                id: 'register',
                text: 'shell.security.registration',
                controller: controllerName,
                action: 'login',
        )
        passwordRecoveryLink = createControl(
                class: Link,
                id: 'passwordRecovery',
                text: 'shell.security.password.recovery',
                controller: controllerName,
                action: 'login',
        )
    }

    void setRegisterUrl(String url) {
        if (!url) {
            register = false
            return
        }

        register = true
        registerLink.url = url
    }

    void setPasswordRecoveryUrl(String url) {
        if (!url) {
            passwordRecovery = false
            return
        }

        passwordRecovery = true
        passwordRecoveryLink.url = url
    }
}
