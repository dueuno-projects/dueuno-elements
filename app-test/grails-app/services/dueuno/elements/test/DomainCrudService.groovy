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

import dueuno.elements.exceptions.ArgsException
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant
import org.grails.datastore.gorm.GormEntity

@CurrentTenant
class DomainCrudService {
    private DetachedCriteria<TCompany> buildQuery(Map filterParams) {
        def query = TCompany.where {}

        if (filterParams.containsKey('id')) query = query.where { id == filterParams.id }

        if (filterParams.find) {
            String search = filterParams.find.replaceAll('\\*', '%')
            query = query.where {
                name =~ "%${search}%"
            }
        }

        // Add additional filters here

        return query
    }

    GormEntity get(GormEntity entity, Serializable id) {
        return entity.get(id)
    }

    List<TCompany> list(Map filterParams = [:], Map fetchParams = [:]) {
        if (!fetchParams.sort) fetchParams.sort = [dateCreated: 'asc']

        // Add single-sided relationships here (Eg. references to other DomainObjects)
        // DO NOT add hasMany relationships, you are going to have troubles with pagination
        fetchParams.fetch = [
                relationshipName: 'join',
        ]

        def query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Integer count(Map filterParams = [:]) {
        def query = buildQuery(filterParams)
        return query.count()
    }

    TCompany create(Map args = [:]) {
        if (args.failOnError == null) args.failOnError = false

        TCompany obj = new TCompany(args)
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    TCompany update(Map args = [:]) {
        Serializable id = ArgsException.requireArgument(args, 'id')
        if (args.failOnError == null) args.failOnError = false

        TCompany obj = get(id)
        obj.properties = args
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    void delete(Serializable id) {
        TCompany obj = get(id)
        obj.delete(flush: true, failOnError: true)
    }
}