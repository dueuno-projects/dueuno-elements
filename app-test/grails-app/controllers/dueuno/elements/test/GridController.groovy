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

import dueuno.elements.components.Form
import dueuno.elements.components.Grid
import dueuno.elements.components.Label
import dueuno.elements.controls.NumberField
import dueuno.elements.controls.TextField
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

        Grid grid1 = c.addComponent(Grid, "grid1")
        grid1.xs = 12
        grid1.sm = 6
        grid1.lg = 3
        grid1.spacing = 2

        for (i in 1..4) {
            def column = grid1.addColumn(
                    class: Form,
                    id: "form${i}",
                    lg: i == 4 ? 12 : i * 2,
            )
            def form = column.component as Form
            form.with {
                grid = true
                addField(
                        class: TextField,
                        id: "text${i}",
                        cols: 6,
                )
                addField(
                        class: NumberField,
                        id: "number${i}",
                        cols: 6,
                )
            }
        }

        def grid2 = c.addComponent(Grid, "grid2")
        grid2.backgroundColor = c.primaryBackgroundColor
        grid2.textColor = c.primaryTextColor
        grid2.setBreakpoints()
        grid2.xs = 6
        grid2.spacing = 4
        grid2.border = true

        for (i in 1..10) {
            grid2.addColumn(
                    class: Label,
                    id: "label${i}",
                    text: "Label ${i}",
                    textAlign: TextAlign.CENTER,
                    displayLabel: false,
                    cssClass: 'd-block',
            )
        }

        display content: c
    }
}
