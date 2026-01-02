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
package dueuno.security

import dueuno.audit.AuditOperation
import dueuno.audit.AuditService
import dueuno.exceptions.ArgsException
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.transactions.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import jakarta.annotation.PostConstruct

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CurrentTenant
@CompileStatic
class AuthenticationProviderService {

    AuditService auditService

    @PostConstruct
    void init() {
        // Executes only once when the application starts
    }

    void install() {
        create(failOnError: true, name: 'Local (Database)', type: AuthenticationProviderType.DAO, sequence: 1)
        create(failOnError: true, name: 'Hardware Token', type: AuthenticationProviderType.HARDWARE, sequence: 2, enabled: false)
        create(failOnError: true, name: 'LDAP / Active Directory', type: AuthenticationProviderType.LDAP, sequence: 3, enabled: false)
        create(failOnError: true, name: 'Google Identity', type: AuthenticationProviderType.OAUTH2, sequence: 4, enabled: false)
        create(failOnError: true, name: 'Microsoft Entra', type: AuthenticationProviderType.OAUTH2, sequence: 5, enabled: false)
        create(failOnError: true, name: 'SAP Identity Authentication Service (IAS)', type: AuthenticationProviderType.OAUTH2, sequence: 6, enabled: false)
    }

    @CompileDynamic
    private DetachedCriteria<TAuthenticationProvider> buildQuery(Map filterParams) {
        def query = TAuthenticationProvider.where {}

        if (filterParams.containsKey('id')) query = query.where { id == filterParams.id }

//        if (filterParams.find) {
//            String search = filterParams.find.replaceAll('\\*', '%')
//            query = query.where {
//                true
//                        || name =~ "%${search}%"
//            }
//        }

        // Add additional filters here

        return query
    }

    private Map getFetchAll() {
        // Add any relationship here (Eg. references to other DomainObjects or hasMany)
        return [
                'relationshipName': 'join',

                // hasMany relationships
                'hasManyRelationship': 'join',
        ]
    }

    private Map getFetch() {
        // Add only single-sided relationships here (Eg. references to other Domain Objects)
        // DO NOT add hasMany relationships, you are going to have troubles with pagination
        return [
                'relationshipName': 'join',
        ]
    }

    TAuthenticationProvider get(Serializable id) {
        return buildQuery(id: id).get(fetch: fetchAll)
    }

    List<TAuthenticationProvider> list(Map filterParams = [:], Map fetchParams = [:]) {
        if (!fetchParams.sort) fetchParams.sort = [sequence: 'asc']
        if (!fetchParams.fetch) fetchParams.fetch = fetch

        def query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Number count(Map filterParams = [:]) {
        def query = buildQuery(filterParams)
        return query.count()
    }

    @Transactional
    TAuthenticationProvider create(Map args = [:]) {
        if (args.failOnError == null) args.failOnError = false
        if (args.enabled == null) args.enabled = true

        TAuthenticationProvider obj = new TAuthenticationProvider(args)
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    @Transactional
    @CompileDynamic
    TAuthenticationProvider update(Map args = [:]) {
        Serializable id = ArgsException.requireArgument(args, 'id')
        if (args.failOnError == null) args.failOnError = false

        TAuthenticationProvider obj = get(id)
        obj.properties = args
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    @Transactional
    void delete(Serializable id) {
        TAuthenticationProvider obj = get(id)
        obj.delete(flush: true, failOnError: true)
        auditService.log(AuditOperation.DELETE, obj)
    }
}
