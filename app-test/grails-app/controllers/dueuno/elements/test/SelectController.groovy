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


import dueuno.elements.components.TableRow
import dueuno.elements.contents.ContentList
import dueuno.elements.controls.Select
import dueuno.elements.core.ElementsController
import grails.gorm.multitenancy.CurrentTenant

@CurrentTenant
class SelectController implements ElementsController {

    def index() {

        def c = createContent(ContentList)
        c.header.removeNextButton()
        c.table.with {
            filters.with {
                fold = false
//                addField(
//                        class: Select,
//                        id: 'company1',
//                        optionsFromRecordset: TCompany.list(),
//                        placeholder: 'Sync load',
//                        cols: 3,
//                )
                addField(
                        class: Select,
                        id: 'company1Default',
                        optionsFromRecordset: TCompany.list(),
                        placeholder: 'Sync load (defaultValue)',
                        defaultValue: 3,
                        cols: 3,
                )
//                addField(
//                        class: Select,
//                        id: 'company2',
//                        onLoad: 'onLoadCompany2',
//                        search: false,
//                        placeholder: 'Async load',
//                        cols: 3,
//                )
//                addField(
//                        class: Select,
//                        id: 'company3',
//                        onLoad: 'onLoadCompany3',
//                        onSearch: 'onSearchCompany3',
//                        placeholder: 'Async search',
//                        cols: 3,
//                )
//                addField(
//                        class: Select,
//                        id: 'company4',
//                        onLoad: 'onLoadCompany4',
//                        multiple: true,
//                        placeholder: 'Multiple',
//                        cols: 3,
//                )
            }

            sortable = [
                    address: 'asc',
                    name   : 'desc',
            ]
            columns = [
                    'company',
                    'name',
                    'picture',
                    'address',
                    'postcode',
                    'salary',
                    'salaryPerMonth',
                    'distanceKm',
                    'dateStart',
                    'dateEnd',
                    'active',
            ]

            rowActions = false
            body.eachRow { TableRow row, Map values ->
            }
        }

        def query = TPerson.where {}

        def filters = c.table.filterParams
        if (filters.name) query = query.where { name =~ "%${filters.name}%" }
        if (filters.company1) query = query.where { company.id == filters.company1 }
        if (filters.company1Default) query = query.where { company.id == filters.company1Default }
        if (filters.company2) query = query.where { company.id in filters.company2.collect { it as Long }  }
        if (filters.company3) query = query.where { company.id in filters.company3.collect { it as Long }  }
        if (filters.company4) query = query.where { company.id in filters.company4.collect { it as Long }  }

        c.table.body = query.list(params)
        c.table.paginate = query.count()

        display content: c
    }

    def onLoadCompany2() {
        def t = createTransition()
        def results = TCompany.list()
        def options = Select.optionsFromRecordset(recordset: results)
        t.set('company2', 'options', options)
        t.setValue('company2', params.company2, false)
        display transition: t
    }

    def onLoadCompany3() {
        def t = createTransition()
        def results = TCompany.get(params.company3)
        def options = Select.optionsFromRecordset(recordset: [results])
        t.set('company3', 'options', options)
        display transition: t
    }

    def onSearchCompany3() {
        def t = createTransition()
        def results = TCompany.where {
            name =~ "%${params.company3}%"
        }.list()
        def options = Select.optionsFromRecordset(recordset: results)
        t.set('company3', 'options', options)
        display transition: t
    }

    def onLoadCompany4() {
        def t = createTransition()
        def results = TCompany.list()
        def options = Select.optionsFromRecordset(recordset: results)
        t.set('company4', 'options', options)
        t.setValue('company4', params.company4, false)
        display transition: t
    }
}
