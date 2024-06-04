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

import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 */

@CompileStatic
class PageRenderProperties implements Serializable {
    Boolean modal
    Boolean wide
    Boolean fullscreen
    Boolean closeButton
    Boolean updateUrl
    String animate
    String scroll

    PageRenderProperties(Map args = [:]) {
        modal = args.modal
        wide = args.wide
        fullscreen = args.fullscreen
        closeButton = args.closeButton
        updateUrl = args.updateUrl
        animate = args.animate
        scroll = args.scroll
    }

    Map asMap() {
        return [
                modal: modal,
                wide: wide,
                fullscreen: fullscreen,
                closeButton: closeButton,
                updateUrl: updateUrl,
                animate: animate,
                scroll: scroll,
        ]
    }
}
