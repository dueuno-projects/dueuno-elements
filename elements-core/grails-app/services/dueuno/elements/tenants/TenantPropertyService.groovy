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
package dueuno.elements.tenants

import dueuno.commons.utils.StringUtils
import dueuno.elements.core.PropertyService
import dueuno.elements.core.PropertyType
import dueuno.elements.exceptions.ArgsException
import dueuno.elements.utils.EnvUtils
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CurrentTenant
class TenantPropertyService extends PropertyService {

    TenantService tenantService

    void install() {
        // Logs
        setBoolean('LOG_ERROR', EnvUtils.isDevelopment())
        setBoolean('LOG_DEBUG', EnvUtils.isDevelopment())
        setBoolean('LOG_TRACE', false)

        // System
        setString('SHELL_URL_MAPPING', '/')

        // GUI icons style (Font Awesome)
        setString('ICON_STYLE', 'fa-solid', 'fa-solid')
        // See: /assets/dueuno/libs/FONTAWESOME-README.TXT, use one of:
        // fa-solid, fa-regular, fa-light, fa-thin, fa-duotone, fa-brand

        // Colors
        setString('PRIMARY_TEXT_COLOR', '#ffffff', '#ffffff')
        setString('PRIMARY_BACKGROUND_COLOR', '#cc0000', '#cc0000')
        setNumber('PRIMARY_BACKGROUND_COLOR_ALPHA', 0.15, 0.15)
        setString('SECONDARY_TEXT_COLOR', '#ffffff', '#ffffff')
        setString('SECONDARY_BACKGROUND_COLOR', '#625b5b', '#625b5b')
        setNumber('SECONDARY_BACKGROUND_COLOR_ALPHA', 1, 1)
        setString('TERTIARY_TEXT_COLOR', '#030303', '#030303')
        setString('TERTIARY_BACKGROUND_COLOR', '#f4f1f1', '#f4f1f1')
        setNumber('TERTIARY_BACKGROUND_COLOR_ALPHA', 1, 1)
    }

    DetachedCriteria<TTenantProperty> buildQuery(Map filters) {
        def query = TTenantProperty.where {}

        if (filters) {
            if (filters.id != null) query = query.where { id == filters.id }
            if (filters.name != null) query = query.where { name == filters.name }
            if (filters.type) query = query.where { type == filters.type }
            if (filters.validation != null) query = query.where { validation != '' }
            if (filters.find) {
                String search = filters.find.replaceAll('\\*', '%')
                query = query.where {
                    true
                            || name =~ "%${search}%"
                            || string =~ "%${search}%"
                            || stringDefault =~ "%${search}%"
                            || filename =~ "%${search}%"
                            || filenameDefault =~ "%${search}%"
                            || directory =~ "%${search}%"
                            || directoryDefault =~ "%${search}%"
                            || url =~ "%${search}%"
                            || urlDefault =~ "%${search}%"
                }
            }
        }

        return query
    }

    TTenantProperty get(Serializable id) {
        Map fetch = [:]
        TTenantProperty p = buildQuery(id: id).get(fetch: fetch)
        if (p) p.refresh()
        return p
    }

    TTenantProperty getByName(String name) {
        Map fetch = [:]
        TTenantProperty p = buildQuery(name: name).get(fetch: fetch)
        if (p) p.refresh()
        return p
    }

    List<TTenantProperty> list(Map filterParams = [:], Map fetchParams = [:]) {
        def query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Integer count(Map filters = [:]) {
        def query = buildQuery(filters)
        return query.count()
    }

    private TTenantProperty create(Map args) {
        if (args.failOnError == null) args.failOnError = false
        TTenantProperty obj = new TTenantProperty(args)
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    private TTenantProperty update(Map args) {
        Serializable id = ArgsException.requireArgument(args, 'id')
        if (args.failOnError == null) args.failOnError = false

        TTenantProperty obj = get(id)
        obj.properties = args
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    @Override
    void setValue(PropertyType type, String name, Object value, Object defaultValue = null, String validation = null) {
        TTenantProperty property = getByName(name)
        String typeName = StringUtils.screamingSnakeToCamel(type as String)
        String typeNameDefault = typeName + 'Default'

        Object oldValue = null

        if (property) {
            oldValue = property[typeName]
            Map updatedProperty = [
                    id        : property.id,
                    (typeName): value,
                    validation: validation ?: property.validation,
            ]
            if (type != PropertyType.PASSWORD) {
                updatedProperty[typeNameDefault] = defaultValue ?: property[typeNameDefault]
            }
            update(updatedProperty)


        } else {
            Map newProperty = [
                    name      : name,
                    type      : type,
                    (typeName): value,
                    validation: validation,
            ]
            if (type != PropertyType.PASSWORD) {
                newProperty[typeNameDefault] = defaultValue
            }
            create(newProperty)
        }

        String tenantId = tenantService.currentTenantId
        if (!inMemoryProperties[tenantId]) inMemoryProperties[tenantId] = [:]
        inMemoryProperties[tenantId][name] = value

        if (onChangeRegistry[name]) {
            log.info "${tenantId} Tenant - Property changed '$name' = '$value'"
            onChangeRegistry[name].call(oldValue, value, defaultValue)
        }

        validateAll()
    }

    @Override
    Object getValue(PropertyType type, String name, Boolean reload = false) {
        String tenantId = tenantService.currentTenantId
        if (inMemoryProperties[tenantId] && inMemoryProperties[tenantId].containsKey(name) && !reload) {
            return inMemoryProperties[tenantId][name]
        }

        String typeName = StringUtils.screamingSnakeToCamel(type as String)
        TTenantProperty property = getByName(name)
        if (!property) {
            return null
        }

        Object value = property[typeName]

        if (!inMemoryProperties[tenantId]) inMemoryProperties[tenantId] = [:]
        inMemoryProperties[tenantId][name] = value

        return value
    }

    void validateAll() {
//        StopWatch sw = new StopWatch()
//        sw.start()

        List<TTenantProperty> properties = list()
        for (property in properties) {
            switch (property.type as PropertyType) {
                case PropertyType.FILENAME:
                    def validation = validateFilename(property.filename)
                    update(id: property.id, validation: validation)
                    break

                case PropertyType.DIRECTORY:
                    def validation = validateDirectory(property.directory)
                    update(id: property.id, validation: validation)
                    break

                case PropertyType.URL:
                    def validation = validateUrl(property.url)
                    update(id: property.id, validation: validation)
                    break
            }
        }

//        sw.stop()
//        log.info "${tenantService.currentTenantId}: Properties validated in ${sw.toString()}"
    }

    void delete(Serializable id) {
        TTenantProperty obj = get(id)
        obj.delete(flush: true, failOnError: true)
    }
}
