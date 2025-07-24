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

import dueuno.commons.utils.FileUtils
import dueuno.elements.ElementsGrailsPlugin
import dueuno.elements.core.*
import dueuno.elements.exceptions.ArgsException
import dueuno.elements.security.SecurityService
import dueuno.elements.security.TRoleGroup
import dueuno.elements.security.TRoleGroupRole
import dueuno.elements.security.TUser
import dueuno.elements.security.TUserRoleGroup
import dueuno.elements.utils.ResourceUtils
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.multitenancy.Tenants
import grails.gorm.multitenancy.WithoutTenant
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.core.connections.ConnectionSource

/**
 * @author Gianluca Sartori
 */

@Slf4j
class TenantService {

    ApplicationService applicationService
    ConnectionSourceService connectionSourceService
    SystemPropertyService systemPropertyService

    void install() {
        create(
                tenantId: defaultTenantId,
                description: 'System tenant',
                deletable: false,
                failOnError: true,
                connectionSource: connectionSourceService.default,
        )
    }

    @CurrentTenant
    String getPrivateDir() {
        String tenantIdUpper = currentTenantId.toUpperCase()
        return systemPropertyService.getDirectory(tenantIdUpper + '_TENANT_PRIVATE_DIR')
    }

    @CurrentTenant
    String getPublicDir() {
        String tenantIdUpper = currentTenantId.toUpperCase()
        return systemPropertyService.getDirectory(tenantIdUpper + '_TENANT_PUBLIC_DIR')
    }

    String getPrivateDir(String tenantId) {
        String tenantIdUpper = tenantId.toUpperCase()
        return systemPropertyService.getDirectory(tenantIdUpper + '_TENANT_PRIVATE_DIR')
    }

    String getPublicDir(String tenantId) {
        String tenantIdUpper = tenantId.toUpperCase()
        return systemPropertyService.getDirectory(tenantIdUpper + '_TENANT_PUBLIC_DIR')
    }

    void eachTenant(Closure closure) {
        List<TTenant> tenantList = list()
        for (tenant in tenantList) {
            Tenants.withId(tenant.tenantId) {
                closure.call(tenant.tenantId)
            }
        }
    }

    void withTenant(String tenantId, Closure closure) {
        Tenants.withId(tenantId) {
            closure.call(tenantId)
        }
    }

    /**
     * Returns the name of the DEFAULT tenant
     * @return the name of the DEFAULT tenant
     */
    static String getDefaultTenantId() {
        return ConnectionSource.DEFAULT
    }

    /**
     * Returns the DEFAULT tenant
     */
    TTenant getDefaultTenant() {
        return getByTenantId(ConnectionSource.DEFAULT)
    }

    /**
     * INTERNAL USE ONLY. Use SecurityService.getCurrentUserTenantId()
     *
     * Returns the name of the current tenantId
     * @return the name of the current tenantId
     */
    @CurrentTenant
    String getCurrentTenantId() {
        return Tenants.currentId()
    }

    /**
     * INTERNAL USE ONLY. Use SecurityService.getCurrentUserTenant()
     *
     * Returns the name of the current tenant
     * @return the name of the current tenant
     */
    @CurrentTenant
    TTenant getCurrentTenant() {
        return getByTenantId(currentTenantId)
    }

    TTenant get(Serializable id) {
        return TTenant.get(id)
    }

    TTenant getByTenantId(String tenantId) {
        return TTenant.findByTenantId(tenantId)
    }

    private DetachedCriteria<TTenant> buildQuery(Map filters) {
        def query = TTenant.where {}

        if (filters) {
//            if (filters.code) query = query.where { code =~ "%${filters.code}%" }
        }

        return query
    }

    List<TTenant> list(Map filterParams = [:], Map fetchParams = [:]) {
        if (!fetchParams.sort) fetchParams.sort = [dateCreated: 'asc']
        def query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Integer count(Map filters = [:]) {
        def query = buildQuery(filters)
        return query.count()
    }

    TTenant create(Map args) {
        if (args.deletable == null) args.deletable = true
        if (args.provision == null) args.provision = false

        TTenant obj = new TTenant(args)
        if (obj.connectionSource.embedded == null) obj.connectionSource.embedded = false
        obj.connectionSource.tenant = true
        obj.connectionSource.readOnly = false
        if (obj.connectionSource.dbCreate == null) obj.connectionSource.dbCreate = 'update'
        obj.connectionSource.name = obj.tenantId
        obj.save(flush: true, failOnError: args.failOnError)

        if (!obj.hasErrors()) {
            log.info "Creating new tenant '${obj.tenantId}'..."

            String tenantIdUpper = obj.tenantId.toUpperCase()
            String root = systemPropertyService.getDirectory('NEW_TENANT_DIR')

            String privateDir = "${root}${obj.tenantId}/private"
            systemPropertyService.setDirectory(tenantIdUpper + '_TENANT_PRIVATE_DIR', privateDir)
            FileUtils.createDirectory(privateDir)
            ResourceUtils.extractDirectory('/deploy/private', privateDir)

            String publicDir = "${root}${obj.tenantId}/public"
            systemPropertyService.setDirectory(tenantIdUpper + '_TENANT_PUBLIC_DIR', publicDir)
            FileUtils.createDirectory(publicDir)
            ResourceUtils.extractDirectory('/deploy/public', publicDir)

            systemPropertyService.validateAll()

            ResourceUtils.extractDirectoryFromPlugin(
                    ElementsGrailsPlugin,
                    ElementsGrailsPlugin.NAME,
                    '/brand',
                    publicDir
            )

            if (obj.tenantId != defaultTenantId) { // Default tenant gets its 'dataSource' from application.yml
                log.info "${obj.tenantId} Tenant - Connecting to database..."
                connectionSourceService.connect(obj.connectionSource)
            }

            if (args.provision) {
                provisionTenant(obj.tenantId)
            }
        }

        return obj
    }

    void provisionTenant(String tenantId) {
        applicationService.executeOnPluginInstall(tenantId)
        applicationService.executeOnInstall(tenantId)
    }

    void provisionTenantUpdate(String tenantId) {
        withTenant(tenantId) {
            applicationService.executeOnUpdate(tenantId)
        }
    }

    TTenant update(Map args) {
        Serializable id = ArgsException.requireArgument(args, 'id')
        if (args.failOnError == null) args.failOnError = false

        TTenant obj = get(id)
        obj.properties = args
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    void delete(Serializable id) {
        TTenant tenant = TTenant.get(id)
        deleteTenantUsersAndGroups(tenant)
        tenant.delete(flush: true, failOnError: true)
        tenant.connectionSource.delete(flush: true, failOnError: true)

        DetachedCriteria<TSystemInstall> systemInstall = TSystemInstall.where { tenantId == tenant.tenantId }
        systemInstall.deleteAll()
    }

    private void deleteTenantUsersAndGroups(TTenant tenant) {
        List<TUserRoleGroup> userRoleGroups = TUserRoleGroup.where { user.tenant == tenant }.list()
        for (userRoleGroup in userRoleGroups) {
            userRoleGroup.delete(flush: true, failOnError: true)
        }

        List<TUser> users = TUser.where { tenant == tenant }.list()
        for (user in users) {
            user.delete(flush: true, failOnError: true)
        }

        List<TRoleGroupRole> roleGroupRoles = TRoleGroupRole.where { roleGroup.tenant == tenant }.list()
        for (roleGroupRole in roleGroupRoles) {
            roleGroupRole.delete(flush: true, failOnError: true)
        }

        List<TRoleGroup> roleGroups = TRoleGroup.where { tenant == tenant }.list()
        for (roleGroup in roleGroups) {
            roleGroup.delete(flush: true, failOnError: true)
        }
    }

}
