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

import dueuno.elements.core.WebRequestAware
import dueuno.elements.security.SecurityService
import grails.artefact.DomainClass
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

import javax.servlet.http.HttpServletRequest
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CurrentTenant
class AuditService implements WebRequestAware {

    SecurityService securityService

    void log(AuditOperation operation, String message) {
        String userAgent = request.getHeader("User-Agent")
        String ipAddress = getClientIpAddress(request).toMapString()

        def auditLog = new TAuditLog(
                ip: ipAddress,
                userAgent: userAgent,
                operation: operation,
                message: message,
                username: securityService.currentUsername ?: 'super',
        )
        auditLog.save(flush: true, failOnError: true)
    }

    void log(AuditOperation operation, DomainClass domainObject) {
        log(operation, domainObject.toString(), getDomainObjectProperties(domainObject).toString())
    }

    private Map<String, String> getDomainObjectProperties(DomainClass domainClass) {
        Map<String, String> results = [id: domainClass['id']]
        domainClass.constrainedProperties.each {
            if (it.value.property.propertyType !in Set) {
                results << [(it.key): domainClass[it.value.property.propertyName]]
            }
        }
        return results
    }

    void log(AuditOperation operation, Class domainObject, String stateBefore, String stateAfter = null) {
        log(operation, domainObject.toString(), stateBefore, stateAfter)
    }

    void log(AuditOperation operation, String objectName, String stateBefore, String stateAfter = null) {
        String userAgent = request.getHeader("User-Agent")
        String ipAddress = getClientIpAddress(request).toMapString()

        def auditLog = new TAuditLog(
                ip: ipAddress,
                userAgent: userAgent,
                operation: operation,
                objectName: objectName,
                stateBefore: stateBefore,
                stateAfter: stateAfter,
                username: securityService.currentUsername ?: 'super',
        )
        auditLog.save(flush: true, failOnError: true)
    }

    private Map getClientIpAddress(HttpServletRequest request) {
        List relevantHeaders = [
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        ]

        Map results = [:]
        for (header in relevantHeaders) {
            String ip = request.getHeader(header)
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                results.put(header, ip)
            }
        }

        results.put("REMOTE_ADDR", request.getRemoteAddr())
        return results
    }

    private DetachedCriteria<TAuditLog> buildQuery(Map filters) {
        def query = TAuditLog.where {
        }

        if (filters) {
            if (filters.dateFrom) query = query.where { dateCreated >= LocalDateTime.of(filters.dateFrom, LocalTime.MIN) }
            if (filters.dateTo) query = query.where { dateCreated <= LocalDateTime.of(filters.dateTo, LocalTime.MAX) }
            if (filters.operation) query = query.where { operation == filters.operation }
            if (filters.username) query = query.where { username == filters.username }
            if (filters.find) {
                String search = filters.find.replaceAll('\\*', '%')
                query = query.where {
                    true
                            || ip =~ "%${search}%"
                            || userAgent =~ "%${search}%"
                            || username =~ "%${search}%"
                            || message =~ "%${search}%"
                            || objectName =~ "%${search}%"
                            || stateBefore =~ "%${search}%"
                            || stateAfter =~ "%${search}%"
                }
            }
//            if (filters.organizationalUnit) query = query.where { organizationalUnit.id == filters.organizationalUnit }
//            if (filters.find) query = query.where {
//                note =~ "%${filters.find}%" ||
//                        firstname =~ "%${filters.find}%" ||
//                        lastname =~ "%${filters.find}%"
//            }
        }

        return query
    }

    List<TAuditLog> list(Map filterParams = [:], Map fetchParams = [:]) {
        if (!fetchParams.sort) fetchParams.sort = [dateCreated: 'desc']
        def query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Integer count(Map filters = [:]) {
        def query = buildQuery(filters)
        return query.count()
    }
}
