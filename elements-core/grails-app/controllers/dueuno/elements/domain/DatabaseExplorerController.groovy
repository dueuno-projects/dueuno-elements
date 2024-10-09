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
package dueuno.elements.domain

import dueuno.elements.components.TableRow
import dueuno.elements.contents.ContentCreate
import dueuno.elements.contents.ContentEdit
import dueuno.elements.contents.ContentList
import dueuno.elements.controls.*
import dueuno.elements.core.Elements
import dueuno.elements.core.ElementsController
import dueuno.elements.style.TextDefault
import dueuno.elements.tenants.TenantService
import dueuno.elements.types.CustomType
import dueuno.elements.types.Types
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@CurrentTenant
class DatabaseExplorerController implements ElementsController {

    TenantService tenantService

    private List<Class> getDomainProperties(Class domainClass) {
        List<Class> results = []
        domainClass.constrainedProperties.each {
            if (it.value.property.propertyType !in Set) {
                results << it
            }
        }
        return results
    }

    private List<String> getDomainColumns(Class domainClass) {
        List<String> results = ['id']
        domainClass.constrainedProperties.each {
            if (it.value.property.propertyType !in Set) {
                results << it.key.toString()
            }
        }
        return results
    }

    private Map<String, String> getDomainFieldNames(Class domainClass) {
        Map<String, String> results = [id: 'id']
        domainClass.constrainedProperties.each {
            if (it.value.property.propertyType !in Set) {
                results << [(it.key): it.key.toString()]
            }
        }
        return results
    }

    def index() {
        def c = createContent(ContentList)

        c.table.filters.with {
            addField(
                    class: Select,
                    id: 'tenantId',
                    optionsFromRecordset: tenantService.list(),
                    keys: ['tenantId'],
                    defaultValue: tenantService.defaultTenantId,
                    cols: 3,
            )
            addField(
                    class: Select,
                    id: 'domainClass',
                    optionsFromList: grailsApplication.domainClasses*.fullName,
                    renderMessagePrefix: false,
                    search: true,
                    cols: 3,
            )
            addField(
                    class: TextField,
                    id: 'find',
                    label: TextDefault.FIND,
                    cols: 6,
            )
        }

        String tenantId = c.table.filterParams.tenantId ?: controllerSession['tenantId'] ?: tenantService.defaultTenantId
        controllerSession['tenantId'] = tenantId

        if (c.table.filterParams.domainClass) {
            Class domainClass = grailsApplication.getDomainClass(c.table.filterParams.domainClass).clazz
            controllerSession['domainClass'] = domainClass

            c.table.with {
                columns = getDomainColumns(domainClass)
                labels = getDomainFieldNames(domainClass)
                sortable = [id: 'asc']

                body.eachRow { TableRow row, Map values ->
                }
            }

            c.table.pagination.reset()

            tenantService.withTenant(tenantId) {
                String searchText = c.table.filterParams.find
                BigDecimal searchNumber
                try {
                    searchNumber = c.table.filterParams.find as Long
                } catch (Exception e) {
                    searchNumber = null
                }

                def query = new DetachedCriteria(domainClass).build {
                    if (searchText) {
                        or {
                            if (searchNumber) {
                                eq 'id', searchNumber
                            }

                            for (property in getDomainProperties(domainClass)) {
                                Class propertyClass = property.value.property.propertyType
                                String propertyName = property.key

                                if (searchText && propertyClass in String) {
                                    like propertyName, "%${searchText}%"

                                } else if (searchText && propertyClass in CustomType && propertyClass['TYPE_VALUE_PROPERTY_TYPE'] == String) {
                                    like propertyName + '.' + propertyClass['TYPE_VALUE_PROPERTY_NAME'], "%${searchText}%"

                                } else if (searchNumber && propertyClass in CustomType && propertyClass['TYPE_VALUE_PROPERTY_TYPE'] == Number) {
                                    eq propertyName + '.' + propertyClass['TYPE_VALUE_PROPERTY_NAME'], searchNumber

                                } else if (searchNumber && propertyClass in Number) {
                                    eq propertyName, searchNumber
                                }
                            }
                        }
                    }
                }

                c.table.body = query.list(c.table.fetchParams)
                c.table.paginate = query.count()
            }
        }

        display content: c
    }

    private buildForm(String tenantId, Class domainClass, Object obj = null) {
        def c = obj
                ? createContent(ContentEdit)
                : createContent(ContentCreate)

        c.header.text = domainClass.simpleName

        c.form.with {
            validate = domainClass

            addField(
                    class: NumberField,
                    id: 'id',
                    label: 'id',
                    readonly: true,
            )

            for (property in getDomainProperties(domainClass)) {
                Class propertyClass = property.value.property.propertyType
                String propertyName = property.key

                if (Types.isRegistered(propertyClass)) {
                    addField(
                            class: propertyClass['TYPE_FIELD'],
                            id: propertyName,
                            label: propertyName,
                    )
                    continue
                }

                if (Elements.isDomainClass(propertyClass)) {
                    addField(
                            class: Select,
                            id: propertyName,
                            label: propertyName,
                            optionsFromRecordset: propertyClass.list(),
                    )
                    continue
                }

                Class fieldClass
                switch (propertyClass) {
                    case String:
                        fieldClass = TextField
                        break

                    case Number:
                        fieldClass = NumberField
                        break

                    case Boolean:
                        fieldClass = Checkbox
                        break

                    case LocalDateTime:
                        fieldClass = DateTimeField
                        break

                    case LocalDate:
                        fieldClass = DateField
                        break

                    case LocalTime:
                        fieldClass = TimeField
                        break

                    default:
                        fieldClass = TextField
                }

                addField(
                        class: fieldClass,
                        id: propertyName,
                        label: propertyName,
                        text: propertyName,
                )

            }
        }

        if (obj) {
            c.form.values = obj
        }

        return c
    }

    def create() {
        String tenantId = controllerSession['tenantId']
        Class domainClass = controllerSession['domainClass']

        if (!domainClass) {
            display message: 'databaseExplorer.select.table.first'
            return
        }

        def c = buildForm(tenantId, domainClass)
        display content: c, modal: true
    }

    def onCreate() {
        String tenantId = controllerSession['tenantId']
        Class domainClass = controllerSession['domainClass']

        tenantService.withTenant(tenantId) {
            def obj = domainClass.newInstance(params)
            obj.save(flush: true)

            if (obj.hasErrors()) {
                display errors: obj
                return
            }

            display action: 'index'
        }
    }

    def edit() {
        String tenantId = controllerSession['tenantId']
        Class domainClass = controllerSession['domainClass']

        tenantService.withTenant(tenantId) {
            def obj = domainClass.get(params.id)
            def c = buildForm(tenantId, domainClass, obj)
            display content: c, modal: true
        }
    }

    def onEdit() {
        String tenantId = controllerSession['tenantId']
        Class domainClass = controllerSession['domainClass']

        tenantService.withTenant(tenantId) {
            def obj = domainClass.get(params.id)
            obj.properties = params
            obj.save(flush: true)

            if (obj.hasErrors()) {
                display errors: obj
                return
            }

            display action: 'index'
        }
    }

    def onDelete() {
        String tenantId = controllerSession['tenantId']
        Class domainClass = controllerSession['domainClass']

        tenantService.withTenant(tenantId) {
            try {
                def obj = domainClass.get(params.id)
                obj.delete(flush: true, failOnError: true)
                display action: 'index'

            } catch (e) {
                display exception: e
            }
        }
    }

}
