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

import grails.util.Holders
import groovy.transform.CompileStatic

import jakarta.servlet.ServletContext

/**
 * Helper trait to access application scoped attributes
 *
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
trait ServletContextAware {

    // We are not serializing the servlet context into the persistent sessions
    transient ServletContext servletContext = (ServletContext) Holders.servletContext

    /**
     * Returns an application scoped variable
     *
     * @param name The name of the attribute
     *
     * @return The application variable
     */
    Object getAttribute(String name) {
        return servletContext.getAttribute(name)
    }

    /**
     * Sets an application scoped variable
     *
     * @param name The name of the variable to set
     *
     * @param value The value of the variable to set
     */
    void setAttribute(String name, Object value) {
        servletContext.setAttribute(name, value)
    }
}
