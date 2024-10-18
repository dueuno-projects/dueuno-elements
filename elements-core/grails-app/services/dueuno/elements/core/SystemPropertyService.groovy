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

import dueuno.commons.utils.FileUtils
import dueuno.commons.utils.StringUtils
import dueuno.elements.exceptions.ArgsException
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.WithoutTenant
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.PostConstruct

/**
 * @author Gianluca Sartori
 */

@Slf4j
@WithoutTenant
class SystemPropertyService extends PropertyService {

    @Autowired
    private ApplicationService applicationService

    void install() {
        // System
        String root = "${FileUtils.workDir}${applicationService.applicationName}/"
        setDirectory('APPLICATION_HOME_DIR', root)
        setDirectory('NEW_TENANT_DIR', "${root}tenants")

        // Languages
        setString('AVAILABLE_LANGUAGES', '')
        setString('EXCLUDED_LANGUAGES', '')
        setString('DEFAULT_LANGUAGE', 'en')

        // Menus
        setBoolean('DISPLAY_MENU', true)
        setBoolean('DISPLAY_MENU_SEARCH', true)
        setBoolean('DISPLAY_HOME_BUTTON', true)
        setBoolean('DISPLAY_USER_MENU', true)

        // Other
        setNumber('FONT_SIZE', 16, 16)
    }

    @PostConstruct
    void init() {
        inMemoryProperties['SYSTEM'] = [:]
    }

    DetachedCriteria<TSystemProperty> buildQuery(Map filters) {
        def query = TSystemProperty.where {}

        if (filters) {
            if (filters.type) query = query.where { type == filters.type }
            if (filters.validation != null) query = query.where { validation != '' }
            if (filters.find) {
                String search = filters.find.replaceAll('\\*', '%')
                query = query.where {
                    name =~ "%${search}%"
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

    TSystemProperty get(Serializable id) {
        TSystemProperty p = TSystemProperty.get(id)
        if (p) p.refresh()
        return p
    }

    private TSystemProperty getByName(String name) {
        TSystemProperty p = TSystemProperty.findByName(name)
        if (p) p.refresh()
        return p
    }

    List<TSystemProperty> list(Map filterParams = [:], Map fetchParams = [:]) {
        def query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Integer count(Map filters = [:]) {
        def query = buildQuery(filters)
        return query.count()
    }

    private TSystemProperty create(Map args) {
        TSystemProperty obj = new TSystemProperty(args)
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    private TSystemProperty update(Map args) {
        Serializable id = ArgsException.requireArgument(args, 'id')
        if (args.failOnError == null) args.failOnError = false

        TSystemProperty.withTransaction {
            TSystemProperty obj = get(id)
            obj.properties = args
            obj.save(flush: true, failOnError: args.failOnError)
            return obj
        }
    }

    @Override
    void setValue(PropertyType type, String name, Object value, Object defaultValue = null, String validation = null) {
        TSystemProperty property = getByName(name)
        String typeName = StringUtils.screamingSnakeToCamel(type as String)
        String typeNameDefault = typeName + 'Default'

        Object oldValue = null

        if (property) {
            oldValue = property[typeName]
            Map updatedProperty = [
                    id: property.id,
                    (typeName): value,
                    validation: validation ?: property.validation,
            ]
            if (typeName != 'password') {
                updatedProperty[typeNameDefault] = defaultValue ?: property[typeNameDefault]
            }
            update(updatedProperty)

        } else {
            Map newProperty = [
                    name: name,
                    type: type,
                    (typeName): value,
                    validation: validation,
            ]
            if (typeName != 'password') {
                newProperty[typeNameDefault] = defaultValue
            }
            create(newProperty)
        }

        inMemoryProperties['SYSTEM'][name] = value
        if (onChangeRegistry[name]) {
            log.info "SYSTEM: Property changed '$name' = '$value'"
            onChangeRegistry[name].call(oldValue, value, defaultValue)
        }

        validateAll()
    }

    @Override
    Object getValue(PropertyType type, String name, Boolean reload = false) {
        if (inMemoryProperties['SYSTEM'].containsKey(name) && !reload) {
            return inMemoryProperties['SYSTEM'][name]
        }

        TSystemProperty property = getByName(name)
        String typeName = StringUtils.screamingSnakeToCamel(type as String)
        String typeNameDefault = typeName + 'Default'

        if (!property) {
            return null
        }

        Object value = property[typeName] == null ? property[typeNameDefault] : property[typeName]
        inMemoryProperties['SYSTEM'][name] = value
        return value
    }

    void validateAll() {
//        StopWatch sw = new StopWatch()
//        sw.start()

        List<TSystemProperty> properties = list()
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
//        log.info "SYSTEM: Properties validated in ${sw.toString()}"
    }
}
