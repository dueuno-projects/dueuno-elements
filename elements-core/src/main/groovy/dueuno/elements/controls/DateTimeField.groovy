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
package dueuno.elements.controls

import dueuno.elements.Component
import dueuno.elements.Control
import dueuno.types.Type
import groovy.transform.CompileStatic

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class DateTimeField extends Control {

    LocalDateTime min
    LocalDateTime max
    Integer timeStep
    Boolean autoPopulate

    DateTimeField(Map args) {
        super(args)

        valueType = Type.DATETIME

        addContainerAttribute('id', id)
        addContainerAttribute('data-td-target-input', 'nearest')
        addContainerAttribute('data-td-target-toggle', 'nearest')

        if (args.min in LocalTime) {
            min = LocalDateTime.of(LocalDate.of(1900, 1, 1), args.min as LocalTime)
        } else if (args.min in LocalDate) {
            min = LocalDateTime.of(args.min as LocalDate, LocalTime.of(0, 0))
        } else if (args.min in LocalDateTime) {
            min = args.min as LocalDateTime
        }

        if (args.max in LocalTime) {
            max = LocalDateTime.of(LocalDate.of(1900, 1, 1), args.max as LocalTime)
        } else if (args.max in LocalDate) {
            max = LocalDateTime.of(args.max as LocalDate, LocalTime.of(0, 0))
        } else if (args.max in LocalDateTime) {
            max = args.max as LocalDateTime
        }

        timeStep = args.timeStep as Integer
        autoPopulate = args.autoPopulate as Boolean ?: false
    }

    @Override
    Component onSubmit(Map args) {
        args.event = 'enter'
        on(args)

        args.event = 'change'
        return on(args)
    }

    @Override
    String getPropertiesAsJSON(Map properties = [:]) {
        Map thisProperties = [
                min         : min?.toString(),
                max         : max?.toString(),
                pattern     : properties.pattern ?: '^[0-9/: ]*$',
                timeStep: timeStep,
                autoPopulate: autoPopulate,
        ]
        return super.getPropertiesAsJSON(thisProperties + properties)
    }
}
