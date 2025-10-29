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

import dueuno.elements.core.Elements
import dueuno.elements.core.WebRequestAware
import dueuno.elements.security.SecurityService
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant
import groovy.json.JsonBuilder
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
        String userAgent = request.getHeader('User-Agent')
        String ipAddress = getClientIpAddress(request)

        def auditLog = new TAuditLog(
                ip: ipAddress,
                userAgent: userAgent,
                operation: operation,
                message: message?.take(2000), // Keep in sync with TAuditLog domain class constraints
                username: securityService.currentUsername ?: 'super',
        )
        auditLog.save(flush: true, failOnError: true)
    }

    void log(AuditOperation operation, Object domainObject) {
        Map properties = Elements.toMap(domainObject)
        log(operation, domainObject.toString(), prettyProperties(properties))
    }

    void log(AuditOperation operation, Class domainObject, Map stateBefore, Map stateAfter = null) {
        String prettyStateBefore = prettyProperties(stateBefore)
        String prettyStateAfter = prettyProperties(stateAfter)
        log(operation, domainObject.toString(), prettyStateBefore, prettyStateAfter)
    }

    void log(AuditOperation operation, Class domainObject, String stateBefore, String stateAfter = null) {
        log(operation, domainObject.toString(), stateBefore, stateAfter)
    }

    void log(AuditOperation operation, String objectName, String stateBefore, String stateAfter = null) {
        String userAgent = request.getHeader('User-Agent')
        String ipAddress = getClientIpAddress(request)

        def auditLog = new TAuditLog(
                ip: ipAddress,
                userAgent: userAgent,
                operation: operation,
                objectName: objectName,
                stateBefore: stateBefore?.take(4000), // Keep in sync with TAuditLog domain class constraints
                stateAfter: stateAfter?.take(4000), // Keep in sync with TAuditLog domain class constraints
                username: securityService.currentUsername ?: 'super',
        )
        auditLog.save(flush: true, failOnError: true)
    }

    private String getClientIpAddress(HttpServletRequest request) {
        List relevantHeaders = [
                'CF-Connecting-IP',        // Cloudflare: IP reale del client dietro il loro CDN
                'True-Client-IP',          // Akamai / altri CDN: IP reale del client
                'X-Forwarded-For',         // De facto standard proxy: lista IP client + proxy
                'X-Real-IP',               // Nginx: IP reale del client
                'X-Cluster-Client-IP',     // Alcuni load balancer / proxy cluster: IP client
                'Client-IP',               // Proxy meno comuni: IP originale del client
                'X-Originating-IP',        // Alcuni webmail / proxy: IP originale del client
                'X-Remote-IP',             // Alcuni proxy privati: IP originale del client
                'X-Client-IP',             // Proxy custom: IP reale del client
                'Proxy-Client-IP',         // Proxy / servlet container legacy: IP client
                'WL-Proxy-Client-IP',      // WebLogic: IP client dietro il proxy
                'X-Forwarded',             // Variante generica di X-Forwarded-For
                'X-Forwarded-Host',        // Non contiene IP, ma può aiutare a ricostruire la connessione
                'Via'                      // RFC 7230: lista dei proxy attraversati, informativo
        ]

        Map results = [:]
        for (header in relevantHeaders) {
            String ip = request.getHeader(header)
            if (ip != null && ip.length() != 0 && !'unknown'.equalsIgnoreCase(ip)) {
                results.put(header, ip)
            }
        }

        results.put('TCP', request.getRemoteAddr() + ':' + request.getRemotePort())
        return results.collect { k, v -> "${k} = ${v}" }.join(', ')
    }

    private String prettyProperties(Map properties) {
        Map<String, String> stringifiedProperties = properties.collectEntries { [(it.key): it.value.toString()] }
        String json = new JsonBuilder(stringifiedProperties).toPrettyString()
        return json
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
