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


import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 */

@CompileStatic
abstract class PageContent extends Component {

    String title
    List titleArgs

    /** These properties can change the display of the content
     * (Eg. display as page or as modal dialog)
     */
    PageRenderProperties renderProperties

    PageContent(Map args = [:]) {
        super(args)

        viewPath = '/dueuno/elements/'
        viewTemplate = 'PageContent'

        String defaultTitle = controllerName + '.' + actionName + '.title'
        title = args.title ?: defaultTitle
        titleArgs = (args.titleArgs == null) ? [] : args.titleArgs as List

        renderProperties = new PageRenderProperties(args)
    }

    void setRenderProperties(Map args) {
        for (property in renderProperties.properties) {
            if (property.key != 'class') {
                String name = property.key
                Object value = args[name]
                renderProperties.setProperty(name, value)
            }
        }
    }

    @Override
    String getPropertiesAsJSON(Map properties = [:]) {
        Map thisProperties = [
                controller: controllerName,
                action: actionName,
                renderProperties: renderProperties.asMap(),
        ]
        return super.getPropertiesAsJSON(thisProperties + properties)
    }

}
