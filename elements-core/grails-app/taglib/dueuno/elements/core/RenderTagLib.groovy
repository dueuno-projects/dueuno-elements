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

import org.apache.commons.lang.time.StopWatch

/**
 * Render tags
 *
 * @author Gianluca Sartori
 */
class RenderTagLib implements WebRequestAware {

    static namespace = "render"
    PageService pageService

    /**
     * Renders a component. Use: <render:component instance="${c}" />
     */
    def component = { Map attrs ->
        Component component = attrs.remove('instance')
        Map properties = (Map) attrs.remove('properties')

        if (component) {
//            StopWatch sw = new StopWatch()
//            sw.start()

            for (property in properties) {
                if (property.key == 'id')
                    continue

                component[property.key] = property.value
            }

            String attributes = attrs.collect {it.key + '="' + it.value + '"' }.join(', ')
            out << render(template: component.getView(), model: component.getModel() + [attributes: attributes])

//            sw.stop()
//            log.info "Rendered component '${component.className} ${component.id}' in ${sw.toString()}"
        }
    }

    /**
     * Renders the sub-components of a component. Use: <render:componentList instance="${c}" />
     */
    def componentList = { attrs ->
        List<Component> components = attrs.instance.components
        for (component in components) {
            String componentView = render(template: component.getView(), model: component.getModel())
            out << componentView
        }
    }

    /**
     * Renders the components transported by a transition. Use: <render:transitionComponentList instance="${c}" />
     */
    def transitionComponentList = { attrs ->
        List<Component> components = attrs.instance.components
        for (component in components) {
            String componentView = '<div>' + render(template: component.getView(), model: component.getModel()) + '</div>'
            out << componentView
        }
    }

    /**
     * Renders a message using the PrettyPrinter engine. Attrs -> code, args
     */
    def message = { attrs ->
        out << PrettyPrinter.message(
                locale,
                attrs.code as String,
                attrs.args as List
        )
    }

    /**
     * Renders an icon
     */
    def icon = { attrs ->
        String cssClass = attrs['class'] ?: ''
        String cssStyle = attrs['style'] ?: ''
        String icon = attrs.icon
        String force = attrs.force ?: ''
        out << '<i class="' + getIconWithStyle(icon, force) + ' ' + cssClass + '" style="' + cssStyle + '" aria-hidden="true"></i>'
    }

    /**
     * Renders an icon css class
     */
    def iconClass = { attrs ->
        String icon = attrs.icon
        String force = attrs.force ?: ''
        out << getIconWithStyle(icon, force)
    }

    private String getIconWithStyle(String icon, String forceStyle) {
        String declaredStyle = icon.find('fa-solid|fa-regular|fa-light|fa-thin|fa-duotone|fa-brand')
        String iconType = declaredStyle ? '' : (forceStyle ?: pageService.iconStyle) + ' '
        return iconType + icon
    }

}
