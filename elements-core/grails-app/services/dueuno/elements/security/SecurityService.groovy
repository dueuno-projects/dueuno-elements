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
package dueuno.elements.security

import dueuno.commons.utils.StringUtils
import dueuno.elements.audit.AuditService
import dueuno.elements.components.ShellService
import dueuno.elements.core.*
import dueuno.elements.exceptions.ArgsException
import dueuno.elements.exceptions.ElementsException
import dueuno.elements.tenants.TTenant
import dueuno.elements.tenants.TenantPropertyService
import dueuno.elements.tenants.TenantService

import dueuno.elements.utils.EnvUtils
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.multitenancy.WithoutTenant
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices

import jakarta.servlet.ServletContext

/**
 * Security API
 *
 * @author Gianluca Sartori
 */

@Slf4j
@WithoutTenant
class SecurityService implements WebRequestAware, LinkGeneratorAware {

    public static final String GROUP_SUPERADMINS = 'SUPERADMINS'
    public static final String GROUP_DEVELOPERS = 'DEVELOPERS'
    public static final String GROUP_ADMINS = 'ADMINS'
    public static final String GROUP_USERS = 'USERS'

    public static final String ROLE_SUPERADMIN = 'ROLE_SUPERADMIN'
    public static final String ROLE_DEVELOPER = 'ROLE_DEVELOPER'
    public static final String ROLE_ADMIN = 'ROLE_ADMIN'
    public static final String ROLE_USER = 'ROLE_USER'

    private static final String USERNAME_SUPERADMIN = 'super'
    private static final String USERNAME_ADMIN = 'admin'

    public static final String DENY_AUTHORIZATION_MESSAGE = 'DENY_AUTHORIZATION_MESSAGE'

    @Autowired
    private ServletContext servletContext

    @Autowired
    private SecurityContextLogoutHandler securityContextLogoutHandler

    @Autowired
    private TokenBasedRememberMeServices tokenBasedRememberMeServices

    @Autowired
    private SpringSecurityService springSecurityService

    @Autowired
    private SystemPropertyService systemPropertyService

    @Autowired
    private TenantPropertyService tenantPropertyService

    @Autowired
    private ApplicationService applicationService

    @Autowired
    private ShellService shellService

    @Autowired
    private TenantService tenantService

    @Autowired
    private AuditService auditService

    void init() {
        applicationService.registerPrettyPrinter(TTenant, '${it.tenantId}')
        applicationService.registerPrettyPrinter(TUser, '${it.fullname}')
        applicationService.registerPrettyPrinter(TRoleGroup, '${it.name}')
        applicationService.registerPrettyPrinter('LANDING_PAGE', 'shell.${it.namespace ? it.namespace + "." : ""}${it.controller}')

        applicationService.registerFeature(
                namespace: 'security',
                controller: 'superadmin',
                icon: 'fa-cog',
                order: 10000000,
                authorities: ['ROLE_SUPERADMIN'],
        )
        applicationService.registerFeature(
                namespace: 'security',
                controller: 'admin',
                icon: 'fa-cog',
                order: 9000000,
                authorities: ['ROLE_ADMIN', 'ROLE_SECURITY'],
        )
    }

    void registerFeatures() {
        registerAdminFeatures()
        registerSuperadminFeatures()
        registerSecurityUserFeatures()
    }

    private void registerSuperadminFeatures() {
        applicationService.registerSuperadminFeature(
                controller: 'tenant',
                icon: 'fa-house-user',
        )
        applicationService.registerSuperadminFeature(
                controller: 'connectionSource',
                icon: 'fa-plug',
        )
        applicationService.registerSuperadminFeature(
                controller: 'systemProperty',
                icon: 'fa-tools',
        )
        applicationService.registerSuperadminFeature(
                controller: 'monitoring',
                icon: 'fa-chart-simple',
                targetNew: true,
        )
        applicationService.registerSuperadminFeature(
                controller: 'sysinfo',
                icon: 'fa-info-circle',
        )
    }

    private void registerAdminFeatures() {
        registerSecurityFeature(
                controller: 'user',
                icon: 'fa-user',
        )
        registerSecurityFeature(
                controller: 'group',
                icon: 'fa-user-shield',
        )
        applicationService.registerAdminFeature(
                controller: 'audit',
                icon: 'fa-eye',
        )
        applicationService.registerAdminFeature(
                controller: 'tenantProperty',
                icon: 'fa-tools',
        )
    }

    private void registerSecurityUserFeatures() {
        applicationService.registerUserFeature(
                namespace: 'security',
                controller: 'userProfile',
                icon: 'fa-user-circle',
                order: 10000010,
        )
        applicationService.registerUserFeature(
                namespace: 'security',
                controller: 'authentication',
                action: 'logout',
                icon: 'fa-power-off',
                confirmMessage: 'shell.security.authentication.logout.confirm',
                direct: true,
                order: 10000020,
        )

        applicationService.registerDeveloperUserFeature(
                order: 10000030,
        )
        applicationService.registerDeveloperUserFeature(
                controller: 'shell',
                action: 'toggleClientLogs',
                icon: 'fa-bug',
                order: 10000040,
        )
        applicationService.registerDeveloperUserFeature(
                controller: 'shell',
                action: 'toggleDevHints',
                icon: 'fa-key',
                order: 10000050,
        )
        applicationService.registerDeveloperUserFeature(
                controller: 'gormExplorer',
                icon: 'fa-table',
                targetNew: true,
                order: 10000060,
        )

        if (EnvUtils.isDevelopment()) {
            applicationService.registerDeveloperUserFeature(
                    controller: 'connectionSource',
                    action: 'h2Console',
                    icon: 'fa-database',
                    targetNew: true,
                    order: 10000070,
            )
        }
    }

    /**
     * Registers a feature into the "System Administration" area.
     * To be used when creating features accessible only by users with the ROLE_SECURITY authority.
     */
    void registerSecurityFeature(Map args) {
        Feature newFeature = applicationService.getFeature(args.controller as String)
        if (!newFeature) {
            args.parent = 'admin'
            args.namespace = 'security'
            args.authorities = ['ROLE_SECURITY']
            applicationService.registerFeature(args)
        }
    }

    void initializeSessionDuration() {
        TUser user = currentUser
        session.maxInactiveInterval = EnvUtils.isDevelopment() ? 10000 : user.sessionDuration * 60 // minutes to seconds
        tokenBasedRememberMeServices.tokenValiditySeconds = user.rememberMeDuration * 60 // minutes to seconds
        tokenBasedRememberMeServices.cookieName = applicationService.applicationName.toUpperCase() + '-REMEMBER-ME'
    }

    void initializeShell() {
        TUser user = currentUser
        String lang = (user?.language in applicationService.languages) ? user.language : tenantPropertyService.getString('DEFAULT_LANGUAGE', true)
        shellService.currentLanguage = lang
        shellService.shell.setUser(currentUsername, user.firstname, user.lastname)
        shellService.setFontSize(user.fontSize)

        setMenuVisibility(shellService.shell.menu)
        setMenuVisibility(shellService.shell.userMenu)
    }

    private void setMenuVisibility(Menu menu) {
        List<Menu> items = menu.items
        for (item in items) {
            List<String> authorities = []
            if (ROLE_SUPERADMIN !in item.authorities) {
                authorities.add(ROLE_SUPERADMIN)
            }
            authorities.addAll(item.authorities)

            item.display = isAnyGranted(authorities as String[])
            setMenuVisibility(item)
        }
    }


    /**
     * Returns true if the specified user has a local account (eg. returns false if the user
     * can access with an LDAP account but cannot access if the LDAP server is not available).
     *
     * @param username a username to check
     *
     * @return true if the specified user has a local account
     */

    Boolean isLocalUser(String username) {
        TUser user = getUserByUsername(username)
        return user ? true : false
    }

    /**
     * Returns true if the currently logged in user has admin authorities
     * @return true if the currently logged in user has admin authorities
     */
    Boolean isAdmin() {
        return isAnyGranted(ROLE_ADMIN)
    }

    /**
     * Returns true if the user with the specified username has admin authorities
     * @return true if the user with the specified username has admin authorities
     */
    Boolean isAdmin(String username) {
        TUser user = getUserByUsername(username)
        return user.authorities.find { it.name == GROUP_ADMINS } != null
    }

    /**
     * Returns true if the currently logged in user has development authorities
     * @return true if the currently logged in user has development authorities
     */
    Boolean isSuperAdmin() {
        return isAnyGranted(ROLE_SUPERADMIN)
    }

    /**
     * Returns true if the currently logged in user has the specified authorities
     *
     * @param roles list of strings representing the authorities to check
     *
     * @return true if the currently logged in user has the specified authorities
     */
    Boolean isAnyGranted(String... roles) {
        return SpringSecurityUtils.ifAnyGranted(roles.join(','))
    }

    /**
     * Returns whether the user has logged in or not
     * @return whether the user has logged in or not
     */
    Boolean isLoggedIn() {
        return springSecurityService.isLoggedIn()
    }

    /**
     * Returns the list of currently logged in user authorities
     * @return the list of currently logged in user authorities
     */
    List getCurrentUserAuthorities() {
        return (List) SpringSecurityUtils.getPrincipalAuthorities()
    }

    /**
     * Returns the currently logged in user
     * @return the currently logged in user
     */
    TUser getCurrentUser(Boolean reload = false) {
        if (!springSecurityService.isLoggedIn()) {
            return null
        }

        TUser currentUser = session['_21CurrentUser'] as TUser
        if (currentUser && !reload) {
            return currentUser
        }

        Object principal = springSecurityService.principal
        TUser user = getUserByUsername(principal.username)
        if (!user) {
            user = createUser(
                    username: principal.username,
                    password: StringUtils.generateRandomToken(),
            )
        }

        session['_21CurrentUser'] = user
        return user
    }

    /**
     * Returns the currently logged in user username
     * @return the currently logged in user username
     */
    String getCurrentUsername() {
        return currentUser?.username
    }

    private String getCurrentUserTenantId() {
        if (currentUser) {
            return currentUser.tenant.tenantId

        } else {
            return tenantService.defaultTenantId
        }
    }

    TTenant getCurrentUserTenant() {
        if (currentUser) {
            return currentUser.tenant

        } else {
            return tenantService.defaultTenant
        }
    }

    /**
     * INTERNAL USE ONLY. Persists the current language for the currently logged in user.
     */
    void saveCurrentUserLanguage() {
        TUser current = getUserByUsername(currentUsername)
        current.language = currentLanguage
        current.save(flush: true, failOnError: true)
    }

    /**
     * INIT - Defines a closure to be executed after the user has logged in
     * @param closure a closure to be executed after the user has logged in
     */
    void afterLogin(@DelegatesTo(SecurityService) Closure closure = { /* No setup */ }) {
        applicationService.registerBootEvent('afterLogin', closure)
    }

    /**
     * INIT - Defines a closure to be executed after the user has logged out
     * @param closure a closure to be executed after the user has logged out
     */
    void afterLogout(@DelegatesTo(SecurityService) Closure closure) {
        applicationService.registerBootEvent('afterLogout', closure)
    }

    /**
     * INTERNAL USE ONLY. Executes custom post-login code.
     */
    @CurrentTenant
    void executeAfterLogin() {
        initializeSessionDuration()
        initializeShell()

        log.debug "Logged in as '${currentUsername}', language '${currentLanguage}', authorised for ${currentUserAuthorities}"
        auditService.log(
                action: 'LOGIN',
                message: "Authorities: ${currentUserAuthorities}",
        )

        // Executes custom login code
        applicationService.executeBootEvents('afterLogin', session)
    }

    void executeLogout() {
        // Equivalent to -> redirect uri: '/springSecurityLogout'
        securityContextLogoutHandler.logout(request, response, null)

        // Handles remember-me cookie
        tokenBasedRememberMeServices.logout(request, response, null)

        //La sessione deve essere invalidata poiché è stata disabilitata l'invalidazione di default (vedere plugin.groovy)
        session.invalidate()
    }

    /**
     * INTERNAL USE ONLY. Executes custom post-logout code.
     */
    @CurrentTenant
    void executeAfterLogout() {
        auditService.log(action: 'LOGOUT', message: "-")
        applicationService.executeBootEvents('afterLogout')
        executeLogout()
    }

    /**
     * Returns the default landing page configured for the current user
     * @return
     */
    @CurrentTenant
    String getLandingPage() {
        if (isAdmin()) // Admins never access to landing pages
            return ''

        TRoleGroup currentUserGroup = currentUser.defaultGroup

        // User configured DEFAULT GROUP
        if (currentUserGroup && currentUserGroup.landingPage) {
            return '/' + currentUserGroup.landingPage

        } else { // USERS Group (applies to all users of a tenant)
            TRoleGroup usersGroup = TRoleGroup.findByTenantAndName(currentUserTenant, GROUP_USERS)
            if (usersGroup && usersGroup.landingPage) {
                return '/' + usersGroup.landingPage
            }
        }

        // Application defined landing page (applies to ALL users)
        return tenantPropertyService.getString('LOGIN_LANDING_URL', true)
    }

    //
    // Users
    //
    TUser getUser(Serializable id) {
        Map fetch = [
                tenant      : 'join',
                defaultGroup: 'join',
        ]

        DetachedCriteria<TUser> query = TUser.where {
            id == id
        }

        return query.get(fetch: fetch)
    }

    TUser getUserByUsername(String username) {
        Map fetch = [
                tenant      : 'join',
                defaultGroup: 'join',
        ]

        DetachedCriteria<TUser> query = TUser.where {
            username == username
        }

        return query.get(fetch: fetch)
    }

    TUser getSuperAdminUser() {
        return getUserByUsername(USERNAME_SUPERADMIN)
    }

    TUser getAdminUser() {
        return getUserByUsername(USERNAME_ADMIN)
    }

    void denyLogin(String message) {
        if (!message) {
            throw new ElementsException("A deny message must be provided.")
        }

        if (session[DENY_AUTHORIZATION_MESSAGE]) {
            return
        }

        session[DENY_AUTHORIZATION_MESSAGE] = message
    }

    private DetachedCriteria<TUser> buildUserQuery(Map filters) {
        def query = TUser.where {
            username != USERNAME_SUPERADMIN
        }

        if (!isSuperAdmin()) {
            String currentTenantId = currentUserTenantId
            query = query.where { tenant.tenantId == currentTenantId }
        }

        if (filters) {
            if (filters.containsKey('id')) query = query.where { id == filters.id }
            if (filters.containsKey('tenant')) query = query.where { tenant.id == filters.tenant }
            if (filters.containsKey('tenantId')) query = query.where { tenant.tenantId == filters.tenantId }
            if (filters.containsKey('deletable')) query = query.where { deletable == filters.deletable }
            if (filters.containsKey('enabled')) query = query.where { enabled == filters.enabled }
            if (filters.username) query = query.where {
                username =~ "%${filters.username}%"
                        || firstname =~ "%${filters.username}%"
                        || lastname =~ "%${filters.username}%"
            }
        }

        return query
    }

    List<TUser> listUserByAuthorities(List authorities, Map params = [:]) {
        //TODO: Quando hai tempo magari se ti ci metti eh? Sartori?
//        def query = TRoleGroupRole.where {
//            role.authority in authorities
//        }
//
//        return query.list(params)
    }

    List<TUser> listAllUser(Map filters = [:], Map params = [:]) {
        if (!params.sort) params.sort = 'lastname'
        def query = buildUserQuery(filters)
        return query.list(params)
    }

    Integer countAllUser(Map filters = [:]) {
        def query = buildUserQuery(filters)
        return query.count()
    }

    List<TUser> listUser(Map filters = [:], Map params = [:]) {
        filters.deletable = true
        filters.tenantId = currentUserTenantId
        return listAllUser(filters, params)
    }

    Integer countUser(Map filters = [:]) {
        filters.deletable = true
        filters.tenantId = currentUserTenantId
        return countAllUser(filters)
    }

    List<String> listUsername(Map filters = [:], Map params = [:]) {
        filters.deletable = true
        filters.tenantId = currentUserTenantId
        return listAllUser(filters, params).username
    }


    /**
     * Encodes the password to be safely persisted on the database
     * @param password the password to encode
     * @return the encoded password
     */
    private String encodePassword(String password) {
        return springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }

    /**
     * Creates a user.
     *
     * @param tenantId the tenant the user will be created in (defaults to 'DEFAULT')
     * @param username the users's username
     * @param password the users's password
     * @param enabled whether the user is enabled or not (defaults to true)
     * @param firstname the users's firstname
     * @param lastname the users's lastname
     * @param email the users's email
     * @param language the users's language (eg: 'it')
     * @param decimalFormat the users's preference about the decimal symbol to use 'ISO_COM' or 'ISO_DOT' (defaults to 'ISO_COM')
     * @param prefixedUnit the users's preference about the unit of measure format: true = KG *** , false = *** KG (defaults to false)
     * @param symbolicCurrency the users's preference about the currency format: true = '€', false = 'EUR' (defaults to true)
     * @param symbolicQuantity the users's preference about the quantity format: true = 't', false = 'TON' (defaults to true)
     * @param invertedMonth the user's preference about the month format: true: MM/DD/AAAA, false: DD/MM/AAAA (defaults to false)
     * @param twelveHours the user's preference about the time format: true: 12:00 PM, false: 24:00 (defaults to false)
     * @param firstDaySunday the user's preference about the week format: true: Sunday, false: Monday (defaults to false)
     * @param groups list of group names the users belongs to
     * @param defaultGroup default group name, used to identify the landing page
     * @param note textual user details or note
     *
     * @return the newly created user
     */

    TUser createUser(Map args) {
        List<String> groups = args.groups ?: []
        String defaultGroup = args.defaultGroup
        if (!defaultGroup && groups.size() > 0) {
            defaultGroup = groups[0]
        }

        TTenant tenant = args.tenant
                ?: tenantService.getByTenantId(args.tenantId as String)
                ?: tenantService.currentTenant

        TUser user = TUser.findByUsername(args.username as String)
        if (user) {
            List userGroups = ((List<TUserRoleGroup>) TUserRoleGroup.findAllByUser(user))*.roleGroup.name
            log.info "User '${args.username}' already exists in groups ${userGroups}, skipping user creation"
            user.errors.rejectValue('username', 'user.username.already.exists', [args.username] as Object[], 'user.username.already.exists')
            return user
        }

        user = new TUser(
                tenant: tenant,
                deletable: args.deletable == null ? true : args.deletable,
                username: args.username,
                password: args.password ? encodePassword((String) args.password) : null,
                enabled: args.enabled == null ? true : args.enabled,
                firstname: args.firstname,
                lastname: args.lastname,
                email: args.email,
                telephone: args.telephone,
                language: args.language ?: 'en',
                decimalFormat: args.decimalFormat ?: PrettyPrinterDecimalFormat.ISO_COM,
                prefixedUnit: args.prefixedUnit == null ? false : args.prefixedUnit,
                symbolicCurrency: args.symbolicCurrency == null ? true : args.symbolicCurrency,
                symbolicQuantity: args.symbolicQuantity == null ? true : args.symbolicQuantity,
                invertedMonth: args.invertedMonth == null ? false : args.invertedMonth,
                twelveHours: args.twelveHours == null ? false : args.twelveHours,
                firstDaySunday: args.firstDaySunday == null ? false : args.firstDaySunday,
                sessionDuration: args.sessionDuration as Integer ?: tenantPropertyService.getNumber('DEFAULT_SESSION_DURATION') ?: 60,
                rememberMeDuration: args.rememberMeDuration as Integer ?: tenantPropertyService.getNumber('DEFAULT_REMEMBER_ME_DURATION') ?: 600,
                fontSize: args.fontSize as Integer ?: systemPropertyService.getNumber('FONT_SIZE') as Integer,
                animations: args.animations as Boolean ?: true,
                defaultGroup: defaultGroup ? TRoleGroup.findByTenantAndName(tenant, defaultGroup) : null,
                note: args.note,
        )
        user.save(flush: true, failOnError: args.failOnError)

        if (user.hasErrors()) {
            log.error "ERROR Creating user '${args.username}' initialised as: ${args}"
            log.error user.errors.toString()

        } else { // Sets the groups
            groups.add(GROUP_USERS)
            if (EnvUtils.isDevelopment()) groups.add(GROUP_DEVELOPERS)
            if (args.admin) groups.add(GROUP_ADMINS)

            for (groupName in groups.unique()) {
                TRoleGroup roleGroup = TRoleGroup.findByName(groupName)
                if (roleGroup) {
                    TUserRoleGroup.create(user, roleGroup)
                } else {
                    log.error "ERROR Creating roleGroup '${groupName}' for user: ${args.username}"
                }
            }

            log.info "${tenant.tenantId}: Created user '${args.username}' in groups: ${groups}"
        }

        return user
    }

    /**
     * Creates a system user. A system user cannot be used to access the application
     * and cannot be edited via the admin GUI.
     *
     * @param args
     * @return the newly created user
     */

    TUser createSystemUser(Map args) {
        args.deletable = false
        if (!args.password) args.password = StringUtils.generateRandomToken(32)
        createUser(args)
    }

    /**
     * Updates an existent user and its groups.
     *
     * @param args see createUser()
     * @return the modified user
     */
    TUser updateUserAndGroups(Map args) {
        String username = (String) ArgsException.requireArgument(args, 'username')
        TTenant tenant = args.tenant
                ?: tenantService.getByTenantId(args.tenantId as String)
                ?: tenantService.currentTenant

        if (args.password) {
            args.password = encodePassword((String) args.password)
        } else {
            args.remove('password')
        }

        args.defaultGroup = TRoleGroup.findByTenantAndName(tenant, args.defaultGroup)

        TUser user = getUserByUsername(username)
        user.properties = args
        user.save(flush: true)

        // Sets the groups
        List groups = args.groups ?: []
        TUserRoleGroup.removeAll(user)

        groups.add(GROUP_USERS)
        if (args.admin) {
            groups.add(GROUP_ADMINS)
        }

        for (groupName in groups) {
            TRoleGroup roleGroup = TRoleGroup.findByTenantAndName(tenant, groupName)
            if (roleGroup) {
                TUserRoleGroup.create(user, roleGroup)
            }
        }

        return user
    }

    /**
     * Updates an existent user.
     *
     * @param args see createUser()
     * @return the modified user
     */

    TUser updateUser(Map args) {
        String username = (String) ArgsException.requireArgument(args, 'username')

        if (args.password) {
            args.password = encodePassword((String) args.password)
        } else {
            args.remove('password')
        }

        TUser user = getUserByUsername(username)
        user.properties = args
        user.save(flush: true)

        return user
    }

    void changeUsername(String oldUsername, String newUsername) {
        TUser user = getUserByUsername(oldUsername)
        user.username = newUsername
        user.save(flush: true, failOnError: true)
    }

    void deleteTenantUsersAndGroups(Serializable tenantId) {
        TTenant tenant = tenantService.get(tenantId)

        DetachedCriteria<TUser> users = TUser.where { tenant == tenant }
        for (user in users) {
            DetachedCriteria<TUserRoleGroup> userRoleGroup = TUserRoleGroup.where { user == user }
            userRoleGroup.deleteAll()
        }
        users.deleteAll()

        DetachedCriteria<TRoleGroup> roleGroups = TRoleGroup.where { tenant == tenant }
        for (roleGroup in roleGroups) {
            DetachedCriteria<TRoleGroupRole> roleGroupRole = TRoleGroupRole.where { roleGroup == roleGroup }
            roleGroupRole.deleteAll()
        }
        roleGroups.deleteAll()
    }

    /**
     * Deletes a user.
     *
     * @param username the username of the user to delete
     */
    void deleteUser(String username) {
        TUser user = getUserByUsername(username)
        TUserRoleGroup.removeAll(user)
        user.delete(flush: true, failOnError: true)
    }

    // TODO: To be implemented
    void resetPassword(String email) {
        TUser user = TUser.findByEmail(email)

        if (user) {
//            notificationService.sendEmail(
//                    email,
//                    applicationService.message('passwordReset.email.subject'),
//                    applicationService.message('passwordReset.email.body', args: [
//                            applicationService.linkApplicationAbsoluteUrl(),
//                            user.id,
//                    ])
//            )
        }
    }

    private DetachedCriteria<TRoleGroup> buildGroupQuery(Map filters) {
        def query = TRoleGroup.where {
            name != GROUP_SUPERADMINS && name != GROUP_ADMINS
        }

        if (!isSuperAdmin()) {
            String currentTenantId = currentUserTenantId
            query = query.where { tenant.tenantId == currentTenantId }
        }

        if (filters.hideUsers) query = query.where { name != GROUP_USERS }
        if (filters.containsKey('id')) query = query.where { id == filters.id }
        if (filters.containsKey('tenant')) query = query.where { tenant.id == filters.tenant }
        if (filters.containsKey('tenantId')) query = query.where { tenant.tenantId == filters.tenantId }
        if (filters.containsKey('name')) query = query.where { name =~ "%${filters.name}%" }
        if (filters.containsKey('deletable')) query = query.where { deletable == filters.deletable }

        return query
    }

    TRoleGroup getGroup(Serializable id) {
        def query = buildGroupQuery(id: id)
        return query.get()
    }

    /**
     * Returns the groups configured for the application
     * @return the groups configured for the application
     */
    List<TRoleGroup> listGroup(Map filters = [:], Map params = [:]) {
        if (!params.sort) params.sort = 'name'
        def query = buildGroupQuery(filters)
        return query.list(params)
    }

    Integer countGroup(Map filters = [:]) {
        def query = buildGroupQuery(filters)
        return query.count()
    }

    /**
     * Creates a group.
     *
     * @param name the name of the group
     * @param authorities the list of authorities assigned to the group
     * @param landingPage a controller name to redirect to when the user logs in
     * @param deletable whether this is a system group or not. System groups cannot be edited by any user.
     *
     * @return the newly created group
     */
    TRoleGroup createGroup(Map args) {
        String groupName = args.name
        List authorities = (List) args.authorities ?: []
        if (authorities in String) authorities = [authorities]
        Boolean deletable = args.deletable == null ? true : args.deletable
        String landingPage = args.landingPage ?: null

        TTenant tenant = args.tenant
                ?: tenantService.getByTenantId(args.tenantId as String)
                ?: tenantService.currentTenant

        Boolean newGroup = false
        TRoleGroup roleGroup = TRoleGroup.findByNameAndTenant(groupName, tenant)
        if (!roleGroup) {
            roleGroup = new TRoleGroup(
                    tenant: tenant,
                    name: groupName,
                    deletable: deletable,
                    landingPage: landingPage,
            )
            roleGroup.save(flush: true)
            newGroup = true
        }

        for (authority in authorities) {
            TRole role = createAuthority((String) authority)
            role.save(flush: true)
            TRoleGroupRole roleGroupRole = TRoleGroupRole.findByRoleGroupAndRole(roleGroup, role)
            if (!roleGroupRole) {
                TRoleGroupRole newRoleGroupRole = new TRoleGroupRole(
                        roleGroup: roleGroup,
                        role: role,
                )
                newRoleGroupRole.save(flush: true)
            }
        }

        if (newGroup && authorities) {
            log.info "${tenant.tenantId}: Created group '$groupName' with authorities: $authorities"
        } else if (authorities) {
            log.info "${tenant.tenantId}: Setting group '$groupName' with authorities: $authorities"
        }

        return roleGroup
    }

     /**
     * Updates a group.
     *
     * @param name the name of the group to edit
     * @param authorities the list of authorities assigned to the group
     * @param landingPage a controller name to redirect to when the user logs in
     *
     * @return the updated group
     */
    TRoleGroup updateGroup(Map args) {
        Map group = ArgsException.requireArgument(args, ['id', 'tenantId'], true)
        if (args.tenantId && !args.name) {
            throw new ElementsException("Please specify the 'name' of the group to update.")
        }

        List authorities = (List) args.authorities ?: []
        if (authorities in String) authorities = [authorities]

        TTenant tenant = TTenant.findByTenantId(args.tenantId)
        TRoleGroup roleGroup = tenant
                ? TRoleGroup.findByTenantAndName(tenant, args.name)
                : TRoleGroup.get(group.id)
        if (!roleGroup) {
            throw new ElementsException("Group '${group.id}' not found!")
        }

        if (args.authorities != null) {
            TRoleGroupRole.removeAll(roleGroup)

            for (authority in authorities) {
                TRole role = TRole.findByAuthority(authority)
                if (!role) role = new TRole(authority: authority).save(flush: true, failOnError: true)
                new TRoleGroupRole(roleGroup: roleGroup, role: role).save(flush: true, failOnError: true)
            }
        }

        roleGroup.properties = args
        roleGroup.save(flush: true, failOnError: args.failOnError)

        return roleGroup
    }

    /**
     * Deletes a group.
     *
     * @param name the name of the group to delete
     */

    void deleteGroup(Serializable id) {
        TRoleGroup roleGroup = TRoleGroup.get(id)
        TRoleGroupRole.removeAll(roleGroup)
        roleGroup.delete(flush: true, failOnError: true)
        log.info "Deleted group '$roleGroup' with authorities $roleGroup.authorities"
    }

    /**
     * Creates an authority.
     *
     * @param authority the authority to create. Must start with 'ROLE_' (eg. 'ROLE_CAN_EDIT').
     *
     * @return the newly created authority
     */

    TRole createAuthority(String authority) {
        TRole role = TRole.findByAuthority(authority)
        if (role)
            return role

        // Role does not exist we create it
        TRole newRole = new TRole(authority: authority).save(flush: true)

        // Set ROLE_SUPERADMIN > ROLE_ADMIN > AUTHORITY so to avoid giving
        // SUPERADMIN and ADMIN permissions to each controller
        // ATTENTION: Modify the code below only if you know what you are doing
        if (newRole.authority !in [ROLE_SUPERADMIN, ROLE_ADMIN, ROLE_DEVELOPER]) {
            new TRoleHierarchyEntry(entry: "${ROLE_ADMIN} > " + newRole.authority).save(flush: true)

        } else if (newRole.authority == ROLE_ADMIN) {
            new TRoleHierarchyEntry(entry: "${ROLE_SUPERADMIN} > ${ROLE_ADMIN}").save(flush: true)

        } else if (newRole.authority == ROLE_DEVELOPER) {
            new TRoleHierarchyEntry(entry: "${ROLE_SUPERADMIN} > ${ROLE_DEVELOPER}").save(flush: true)
        }

        springSecurityService.reloadDBRoleHierarchy()
        log.info "Created authority '$authority'"

        newRole.save(flush: true)
        return newRole
    }

    /**
     * Returns the authorities configured for the application
     * @return the authorities configured for the application
     */

    List<String> listAuthority() {
        List<String> results = []
        for (role in TRole.findAll()) {
            if (role.authority != ROLE_SUPERADMIN &&
                    role.authority != ROLE_ADMIN &&
                    role.authority != ROLE_USER)
                results.add(role.authority)
        }
        return results
    }

    String getAdminUsername(String tenantId) {
        if (tenantId == tenantService.defaultTenantId) {
            return 'admin'

        } else {
            TTenant tenant = tenantService.getByTenantId(tenantId)
            return tenant.tenantId.toLowerCase() + 'admin'
        }
    }

    void installTenantSecurity(String tenantId) {
        createGroup(tenantId: tenantId, name: GROUP_USERS, authorities: [ROLE_USER], deletable: false)
        createGroup(tenantId: tenantId, name: GROUP_DEVELOPERS, authorities: [ROLE_DEVELOPER], deletable: false)
        createGroup(tenantId: tenantId, name: GROUP_ADMINS, authorities: [ROLE_ADMIN], deletable: false)
        createAuthority('ROLE_SECURITY')

        if (tenantId == 'DEFAULT') {
            createGroup(tenantId: tenantService.defaultTenantId, name: GROUP_SUPERADMINS, authorities: [ROLE_SUPERADMIN], deletable: false)
            createSystemUser(
                    tenantId: tenantService.defaultTenantId,
                    groups: [GROUP_SUPERADMINS],
                    firstname: 'Super',
                    lastname: 'Admin',
                    username: USERNAME_SUPERADMIN,
                    password: USERNAME_SUPERADMIN,
                    sessionDuration: 5, // always 5 minutes for the SuperAdmin
                    rememberMeDuration: 5, // always 5 minutes for the SuperAdmin
            )
        }

        String username = getAdminUsername(tenantId)
        createSystemUser(
                tenantId: tenantId,
                username: username,
                password: username,
                firstname: 'Admin',
                lastname: tenantId,
                sessionDuration: 15, // defaults to 15 minutes for the Admin
                rememberMeDuration: 15, // defaults to 15 minutes for the Admin
                admin: true,
        )

        tenantPropertyService.setBoolean('USER_CAN_CHANGE_PASSWORD', true)

        tenantPropertyService.setNumber('DEFAULT_SESSION_DURATION', 60)
        tenantPropertyService.setNumber('DEFAULT_REMEMBER_ME_DURATION', 600)

        tenantPropertyService.setBoolean('LOGIN_REMEMBER_ME', false)
        tenantPropertyService.setBoolean('LOGIN_AUTOCOMPLETE', true)
        tenantPropertyService.setString('LOGIN_LANDING_URL', '')
        tenantPropertyService.setString('LOGOUT_LANDING_URL', '')
        tenantPropertyService.setString('LOGIN_REGISTRATION_URL', '')
        tenantPropertyService.setString('LOGIN_PASSWORD_RECOVERY_URL', '')
        tenantPropertyService.setString('LOGIN_COPY', 'Copyright &copy; <a href="https://dueuno.com">Dueuno</a><br/>All rights reserved')

        tenantPropertyService.setString('LOGIN_BACKGROUND_IMAGE', servletContext.contextPath + linkPublicResource(tenantId, '/brand/login-background.jpg', false, false))
        tenantPropertyService.setString('LOGIN_LOGO', servletContext.contextPath + linkPublicResource(tenantId, '/brand/login-logo.png', false, false))
    }
}
