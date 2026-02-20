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
import groovy.util.logging.Slf4j

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
abstract class Page extends Component {

    KeyPress keyPress
    PageModal modal
    PageMessageBox messageBox
    transient PageContent content

    String favicon
    String appicon

    Page(Map args = [:]) {
        super(args)

        viewPath = '/dueuno/elements/pages/'

        modal = addComponent(PageModal, 'modal', args)
        messageBox = addComponent(PageMessageBox, 'messageBox', args)
        keyPress = addComponent(KeyPress, 'keyPress', args.keyPress as Map)

        favicon = args.favicon
        appicon = args.appicon
    }

    /**
     * Internal use only. Used in GSPs to include assets from third party elements (See _Shell.gsp)
     * @return
     */
    List<String> getComponentsRegistry() {
        return Elements.componentsRegistry
    }
}
