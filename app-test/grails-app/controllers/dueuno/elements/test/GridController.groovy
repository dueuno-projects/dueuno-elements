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
package dueuno.elements.test

import dueuno.elements.components.Grid
import dueuno.elements.components.Label
import dueuno.elements.core.ApplicationService
import dueuno.elements.core.ElementsController
import dueuno.elements.style.TextAlign
import dueuno.elements.types.QuantityService
import grails.gorm.multitenancy.CurrentTenant

@CurrentTenant
class GridController implements ElementsController {

    ApplicationService applicationService
    QuantityService quantityService

    def index() {
        def c = createContent()
        c.header.removeNextButton()

        def grid = c.addComponent(Grid)
        grid.xs = 12
        grid.sm = 6
        grid.spacing = 0
        grid.border = false

        for (i in 1..10) {
            grid.addComponent(
                    class: Label,
                    id: "label${i}",
                    text: "Label ${i}",
                    textAlign: TextAlign.CENTER,
                    displayLabel: false,
                    backgroundColor: 'blue',
                    textColor: 'white',
                    cssClass: 'd-block',
            )
        }

        def grid2 = c.addComponent(Grid, "grid2")
        grid2.setBreakpoints()
        grid2.spacing = 3
        grid2.border = true

        for (i in 1..10) {
            grid2.addComponent(
                    class: Label,
                    id: "label${i}",
                    text: "Label ${i}",
                    textAlign: TextAlign.CENTER,
                    displayLabel: false,
                    backgroundColor: 'red',
                    textColor: 'white',
                    cssClass: 'd-block',
            )
        }

        display content: c
    }
}