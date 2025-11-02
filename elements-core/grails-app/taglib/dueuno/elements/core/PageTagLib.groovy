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

import javax.servlet.ServletContext

/**
 * Page tags
 *
 * @author Gianluca Sartori
 */
class PageTagLib {

    static namespace = "page"

    ServletContext servletContext

    /**
     * Renders default elements header. Use: <page:header component="${c}" />
     */
    def header = { Map attrs ->
        Component component = attrs.component
        out << render(template: '/dueuno/elements/core/PageHeader', model: component.getModel())
    }

    /**
     * Renders default color styles. Use: <page:colors component="${c}" />
     */
    def colors = { Map attrs ->
        Component component = attrs.component
        out << render(template: '/dueuno/elements/core/PageColors', model: component.getModel())
    }

    /**
     * Renders default elements footer. Use: <page:footer component="${c}" />
     */
    def footer = { Map attrs ->
        Component component = attrs.component
        out << render(template: '/dueuno/elements/core/PageFooter', model: component.getModel())
    }

    /**
     * Initializes JS. Use: <page:initialize />
     */
    def initialize = { Map attrs ->
        out << render(template: '/dueuno/elements/core/PageInitialize', model: null)
    }

    /**
     * Loading screen. Use: <page:loading />
     */
    def loading = { Map attrs ->
        out << render(template: '/dueuno/elements/core/PageLoading', model: null)
    }

    def contextPath = { Map attrs ->
        out << servletContext.contextPath
    }

}
