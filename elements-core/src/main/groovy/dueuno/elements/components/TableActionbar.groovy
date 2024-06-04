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
package dueuno.elements.components

import dueuno.elements.core.Component
import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class TableActionbar extends Component {

    Table table
    TableFilters filters
    Button actions

    TableActionbar(Map args) {
        super(args)

        table = args.table as Table
        filters = args.filters as TableFilters

        actions = createControl(
                class: Button,
                id: 'actions',
                dontCreateDefaultAction: true,
        )
    }

    Component addAction(Map args) {
        String controller = args.controller ?: controllerName
        String action = args.action ?: 'index'

        args.id = args.id ?: (controller == controllerName ? action : controller + action?.capitalize())
        args.controller = controller
        args.action = action

        filters.display = true

        return actions.addAction(args + [
                controller: controller,
                action    : action,
        ])
    }

    Component addSeparator(String text = null) {
        return actions.addSeparator(text)
    }
}