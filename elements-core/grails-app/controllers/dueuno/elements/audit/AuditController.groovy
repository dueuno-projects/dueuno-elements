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
package dueuno.elements.audit

import dueuno.elements.contents.ContentList
import dueuno.elements.controls.DateField
import dueuno.elements.controls.Select
import dueuno.elements.controls.TextField
import dueuno.elements.core.ElementsController
import dueuno.elements.security.SecurityService
import dueuno.elements.style.TextDefault
import grails.plugin.springsecurity.annotation.Secured

/**
 * @author Gianluca Sartori
 */
@Secured(['ROLE_ADMIN'])
class AuditController implements ElementsController {

    AuditService auditService
    SecurityService securityService

    def index() {
        def c = createContent(ContentList)
        c.header.removeNextButton()
        c.table.with {
            filters.with {
                fold = false
                addField(
                        class: DateField,
                        id: 'dateFrom',
                        cols: 2,
                )
                addField(
                        class: DateField,
                        id: 'dateTo',
                        cols: 2,
                )
                addField(
                        class: Select,
                        id: 'username',
                        optionsFromList: securityService.listUsername(),
                        renderMessagePrefix: false,
                        cols: 2,
                )
                addField(
                        class: Select,
                        id: 'operation',
                        optionsFromEnum: AuditOperation,
                        cols: 2,
                )
                addField(
                        class: TextField,
                        id: 'find',
                        label: TextDefault.FIND,
                        cols: 4,
                )
            }
            //columnsFromClass = TAuditLog
            sortable = [
                dateCreated: 'desc',
            ]
            columns = [
                    'dateCreated',
                    'username',
                    'operation',
                    'message',
                    'dataObject',
                    'dataBefore',
                    'dataAfter',
                    'ip',
                    'userAgent',
            ]
            displayActions = false
        }

        def filters = c.table.filterParams
        c.table.body = auditService.list(filters, params)
        c.table.paginate = auditService.count(filters)

        display content: c
    }

}
