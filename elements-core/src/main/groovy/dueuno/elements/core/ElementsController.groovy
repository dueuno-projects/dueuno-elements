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

import dueuno.commons.utils.LogUtils
import dueuno.elements.contents.ContentHeader
import dueuno.elements.exceptions.ElementsException
import dueuno.elements.pages.PageBlank
import grails.artefact.Controller
import grails.artefact.Enhances
import grails.validation.Validateable
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.validation.Errors

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
@Enhances("Controller")
trait ElementsController implements Controller, ServletContextAware, WebRequestAware, LinkGeneratorAware {

    private Logger log = LoggerFactory.getLogger(ElementsController)

    @CompileDynamic
    Boolean getDisplay() {
        try {
            render transition()
        } catch (Exception ignore) {
            // no-op
        }
        return true
    }

    @CompileDynamic
    void display(Map args = [:]) {
        if (!args.page && requestParams._21Transition) {
//            StopWatch sw = new StopWatch()
//            sw.start()
            try {
                render transition(args)
            } catch (Exception ignore) {
                // no-op
            }
//            sw.stop()
//            log.info "Rendered TRANSITION in ${sw.toString()}, args: ${args}"

        } else { // When the user hits the browser REFRESH button
//            StopWatch sw = new StopWatch()
//            sw.start()
            try {
                render page(args)
            } catch (Exception ignore) {
                // no-op
            }
//            sw.stop()
//            log.info "Rendered PAGE in ${sw.toString()}, args: ${args}"
        }
    }

    private PageService getPageService() {
        return Elements.getBean('pageService') as PageService
    }

    /**
     * Returns an instance of a specific Page class.
     *
     * @classOrName Class to instantiate
     * @return an instance of a Page
     */
    public <T> T createPage(Class<T> clazz, Map args = [:]) {
        return getPageService().createPage(clazz, args)
    }

    /**
     * Creates a ContentBase
     * @return an instance of a ContentBase
     */
    ContentHeader createContent() {
        return getPageService().createContent()
    }

    /**
     * Returns an instance of a specific PageContent class.
     *
     * @classOrName PageContent class to instantiate
     * @return an instance of a Content
     */
    public <T> T createContent(Class<T> clazz, Map args = [:]) {
        return getPageService().createContent(clazz, args)
    }

    /**
     * Creates a Transition
     * @return a Transition instance
     */
    Transition createTransition() {
        return getPageService().createTransition()
    }

    OutputStream getDownloadOutputStream(String pathname, Boolean inline = false) {
        return getPageService().getDownloadOutputStream(pathname, inline)
    }

    void download(String pathname, Boolean inline = false) {
        getPageService().download(pathname, inline)
    }

    private Map transition(Map args = [:]) {
        Transition t = args.transition as Transition ?: createTransition()

        if (args.loading != null) {
            t.loading(args.loading as Boolean)
        }

        if (args.content) {
            PageContent content = args.content as PageContent
            content.setRenderProperties(args)
            t.renderContent(content)

        } else if (args.message) {
            String message = args.message as String
            t.infoMessage(message, args)

        } else if (args.exception) {
            Exception e = args.exception as Exception
            log.error LogUtils.logStackTrace(e)
            t.errorMessage(e.message, args)

        } else if (args.errorMessage) {
            String message = args.errorMessage as String
            t.errorMessage(message, args)

        } else if (args.errors) {
            Integer submittedComponentCount = requestParams._21SubmittedCount as Integer
            String submittedComponentName = requestParams._21SubmittedName as String
            Object obj = args.errors

            if (submittedComponentCount > 1) { // Multiple components submitted
                Object componentErrors = args.errors
                if (componentErrors !in Map) {
                    throw new ElementsException("Multiple components submitted, please set 'errors' as a Map (Eg. 'display errors: [formName1: obj1, formName2: obj2, ...]'")
                }

                for (component in componentErrors as Map) {
                    String componentName = component.key
                    Object componentError = component.value
                    Errors errors = getValidationErrors(componentName, componentError)
                    t.set(componentName, 'errors', errors)
                }

            } else if (submittedComponentCount == 1) { // Single component submitted
                Errors errors = getValidationErrors(submittedComponentName, obj)
                t.set(submittedComponentName, 'errors', errors)

            } else { // no component submitted
                Errors errors = obj['errors'] as Errors
                if (errors.globalError) {
                    t.errorMessage(errors.globalError.codes[1])
                } else {
                    t.errorMessage(errors.allErrors.join('. '))
                }
            }

        } else if (args.controller || args.action) {
            t.redirect(args)
        }

        return [
                template: t.view,
                model   : t.model,
        ]
    }

    private Errors getValidationErrors(String componentName, Object validateable) {
        if (!validateable.hasProperty('errors') && validateable['errors'] !in Errors) {
            throw new ElementsException("Cannot use object '${validateable}' to display errors. Please specify an instance of an object implementing '${Validateable.name}'")
        }

        Errors errors = validateable['errors'] as Errors
        for (error in errors.allErrors) {
            println "[${componentName}] " + message(error.defaultMessage, error.arguments)
        }

        return errors
    }

    private Map page(Map args) {
        Page p

        if (args.page) {
            p = args.page as Page
            args.remove('page')
        } else {
            p = getMainPage()
        }

        if (args.content) {
            PageContent content = args.content as PageContent
            p.content = content
        }

        return [
                template: p.view,
                model   : p.model + args,
        ]
    }

    private Page getMainPage() {
        return getPageService().mainPage ?: createPage(PageBlank)
    }
}
