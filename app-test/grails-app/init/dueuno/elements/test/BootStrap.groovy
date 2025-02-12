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

import dueuno.commons.utils.FileUtils
import dueuno.elements.components.ShellService
import dueuno.elements.core.ApplicationService
import dueuno.elements.core.ConnectionSourceService
import dueuno.elements.core.SystemPropertyService
import dueuno.elements.core.TransitionService
import dueuno.elements.security.SecurityService
import dueuno.elements.tenants.TenantPropertyService
import dueuno.elements.tenants.TenantService
import dueuno.elements.types.Money
import dueuno.elements.types.QuantityService
import grails.web.servlet.mvc.GrailsHttpSession

import javax.servlet.ServletContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class BootStrap {

    ServletContext servletContext
    ApplicationService applicationService
    QuantityService quantityService
    SystemPropertyService systemPropertyService
    TenantPropertyService tenantPropertyService
    TenantService tenantService
    SecurityService securityService
    ConnectionSourceService connectionSourceService
    ShellService shellService
    TransitionService transitionService

    def init = {
        applicationService.onUpdate('2021-10-03') { String tenantId ->
            println "${tenantId}: UPDATE N.2"
        }

        applicationService.onUpdate('2021-10-02') { String tenantId ->
            println "${tenantId}: UPDATE N.1"
        }

        applicationService.onUpdate('2021-10-05') { String tenantId ->
            println "${tenantId}: UPDATE N.4"
        }

        applicationService.onUpdate('2021-10-04') { String tenantId ->
            println "${tenantId}: UPDATE N.3"
        }

        applicationService.onSystemInstall {
            systemPropertyService.setBoolean('DISPLAY_MENU', true)
            systemPropertyService.setString('DEFAULT_LANGUAGE', 'it')
            systemPropertyService.setString('EXCLUDED_LANGUAGES', 'de,pi')
            systemPropertyService.setString('TEST_JOB_PROPERTY', 'CHANGE ME')

            connectionSourceService.create(
                    name: 'dynamicDatasource',
                    driverClassName: 'org.h2.Driver',
                    dbCreate: 'update',
                    username: 'sa',
                    password: '',
                    url: 'jdbc:h2:mem:DYNAMIC_CONNECTION;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=TRUE',
            )

            tenantService.create(
                    tenantId: 'TEST',
                    description: 'Test tenant',
                    failOnError: true,
                    connectionSource: [
                            driverClassName: 'com.mysql.cj.jdbc.Driver',
                            url            :'jdbc:mysql://localhost:3306/dueuno_elements_test?useSSL=false&createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC',
                            username       : 'root',
                            password       : 'root',
                    ]
            )
        }

        applicationService.onInstall { String tenantId ->

            String appLink = servletContext.contextPath
            tenantPropertyService.setString('SHELL_URL_MAPPING', '/admin')
            tenantPropertyService.setString('LOGIN_LANDING_URL', '/')
            tenantPropertyService.setString('LOGIN_REGISTRATION_URL', appLink + '/crud/register')
            tenantPropertyService.setString('LOGIN_PASSWORD_RECOVERY_URL', appLink + '/crud/passwordRecovery')
            tenantPropertyService.setString(
                    'LOGIN_COPY',
                    'Copyright &copy; Dueuno.<br/>All rights reserved.<br/><br/><a href="' + appLink + '">&lt;&lt; Back to website</a>'
            )

            securityService.updateGroup(
                    tenantId: tenantId,
                    name: 'USERS',
                    landingPage: 'table',
            )
            securityService.createGroup(
                    tenantId: tenantId,
                    name: 'PROJECT MANAGER',
                    authorities: ['ROLE_PROJECT_MANAGER'],
            )
            securityService.createUser(
                    tenantId: tenantId,
                    firstname: "Developer",
                    username: tenantId == tenantService.defaultTenantId ? 'dev' : "${tenantId}/dev",
                    password: 'dev',
                    groups: ['DEVELOPERS', 'PROJECT MANAGER'],
            )

            FileUtils.createDir(tenantService.publicDir + 'upload')

            quantityService.enableUnitLength(['KM', 'M'])

            // Creating a new tenant from the GUI gives an error here, to solve it you need to
            // remove the 'spring-dev-tools' dependency in build.gradle
            // See: https://github.com/spring-projects/spring-data-jpa/issues/2552
            TDemo demo = new TDemo(
                    textfield: 'Ventuno',
                    numberfield: 21,
                    checkbox: true,
                    moneyfield: new Money(21),
            ).save(flush: true)
            TCompany none = new TCompany(name: '000.no.company').save(flush: true)
            TCompany dueuno = new TCompany(name: 'Dueuno S.r.l.').save(flush: true)
            TCompany duedue = new TCompany(name: 'Duedue S.r.l.').save(flush: true)
            TPerson user1 = new TPerson(active: true, company: dueuno, name: 'user1', address: 'Via del Primo', postcode: 30020).save(flush: true)
            TPerson user2 = new TPerson(active: true, company: dueuno, name: 'user2', address: 'Via del Secondo', postcode: 30020).save(flush: true)
            TPerson user3 = new TPerson(active: true, company: dueuno, name: 'user3', address: 'Via del Terzo', postcode: 30020).save(flush: true)
            TPerson admin = new TPerson(active: true, company: dueuno, name: 'admin', address: 'Via dell\'Admin', postcode: 30020).save(flush: true)

            dueuno.addToEmployees(user1).save(flush: true, failOnError: true)
            dueuno.addToEmployees(user2).save(flush: true, failOnError: true)
            dueuno.addToEmployees(user3).save(flush: true, failOnError: true)

            new TFruit(name: 'Acerola', image: 'acerola_small.png').save(flush: true)
            new TFruit(name: 'Apple', image: 'apple_small.png').save(flush: true)
            new TFruit(name: 'Apricots', image: 'apricot_small.jpg').save(flush: true)
            new TFruit(name: 'Avocado', image: 'avocado_small.jpg').save(flush: true)
            new TFruit(name: 'Banana', image: 'banana_small.png').save(flush: true)
            new TFruit(name: 'Blackberry', image: 'blackberry_small.jpg').save(flush: true)
            new TFruit(name: 'Blackcurrant', image: 'blackcurrant_small.png').save(flush: true)
            new TFruit(name: 'Blueberries', image: 'blueberries_small.jpg').save(flush: true)
            new TFruit(name: 'Breadfruit', image: 'breadfruit_small.png').save(flush: true)
            new TFruit(name: 'Cantaloupe', image: 'cantaloupe_small.png').save(flush: true)
            new TFruit(name: 'Carambola', image: 'carambola_small.jpg').save(flush: true)
            new TFruit(name: 'Cherimoya', image: 'cherimoya_small.png').save(flush: true)
            new TFruit(name: 'Cherries', image: 'cherries_small.jpg').save(flush: true)
            new TFruit(name: 'Clementine', image: 'clementine_small.png').save(flush: true)
            new TFruit(name: 'Coconut Meat', image: 'coconutmeat_small.png').save(flush: true)
            new TFruit(name: 'Cranberries', image: 'cranberries_small.jpg').save(flush: true)
            new TFruit(name: 'Custard-Apple', image: 'custardapple_small.png').save(flush: true)
            new TFruit(name: 'Date Fruit', image: 'datefruit_small.png').save(flush: true)
            new TFruit(name: 'Durian', image: 'durian_samll.png').save(flush: true)
            new TFruit(name: 'Elderberries', image: 'elderberries_small.png').save(flush: true)
            new TFruit(name: 'Feijoa', image: 'feijoa_small.gif').save(flush: true)
            new TFruit(name: 'Figs', image: 'fig_small.png').save(flush: true)
            new TFruit(name: 'Gooseberries', image: 'gooseberries_small.png').save(flush: true)
            new TFruit(name: 'Grapefruit', image: 'grapefruit_samll.png').save(flush: true)
            new TFruit(name: 'Grapes', image: 'grapes_small.jpg').save(flush: true)
            new TFruit(name: 'Guava', image: 'guava_small.jpg').save(flush: true)
            new TFruit(name: 'Honeydew Melon', image: 'honeydewmelon_small.gif').save(flush: true)
            new TFruit(name: 'Jackfruit', image: 'jackfruit_small.gif').save(flush: true)
            new TFruit(name: 'Java-Plum', image: 'javplum_small.png').save(flush: true)
            new TFruit(name: 'Jujube Fruit', image: 'jujube_small.png').save(flush: true)
            new TFruit(name: 'Kiwifruit', image: 'kiwi_small.gif').save(flush: true)
            new TFruit(name: 'Kumquat', image: 'kumquat_small.png').save(flush: true)
            new TFruit(name: 'Lemon', image: 'lemon_small.gif').save(flush: true)
            new TFruit(name: 'Lime', image: 'lime_small.jpg').save(flush: true)
            new TFruit(name: 'Longan', image: 'longan_small.png').save(flush: true)
            new TFruit(name: 'Loquat', image: 'loquat_small.png').save(flush: true)

        }

        applicationService.onDevInstall { String tenantId ->
            println "${tenantId}: INSTALLING STUFF ONLY WHEN IN DEVELOPMENT ENVIRONMENT"

            systemPropertyService.setBoolean('TEST_DENY_LOGIN', false)

            systemPropertyService.setNumber('TEST_NUMBER', 5 * 60)
            systemPropertyService.setString('TEST_STRING', 'This is a string', 'This is its default value')
            systemPropertyService.setPassword('TEST_PASSWORD', 'ThisIsAPassword!')
            systemPropertyService.setDateTime('TEST_DATE_TIME', LocalDateTime.now())
            systemPropertyService.setDate('TEST_DATE', LocalDate.now())
            systemPropertyService.setTime('TEST_TIME', LocalTime.now())
            systemPropertyService.setBoolean('TEST_BOOLEAN', true)
            systemPropertyService.setDirectory('TEST_DIRECTORY', '', '/pippo/pluto')
            systemPropertyService.setFilename('TEST_FILENAME', '', '\\pippo\\pluto\\config.txt')
            systemPropertyService.setUrl('TEST_URL', 'http://www.dueuno.com', 'http://www.google.com/search')
            systemPropertyService.setDirectory('TEST_WRONG_DIRECTORY', '\\this\\directory\\doesnt\\exist', '')
            systemPropertyService.setFilename('TEST_WRONG_FILENAME', '\\this\\file\\doesnt\\exist.txt', '')
            systemPropertyService.setUrl('TEST_WRONG_URL', 'htp://www.wrong.url', '')
            systemPropertyService.setString('TEST_ON_CHANGE', 'Change me and check the console!', '')

            securityService.createAuthority('ROLE_1')
            securityService.createAuthority('ROLE_2')
            securityService.createAuthority('ROLE_3')

            securityService.createGroup(
                    tenantId: tenantId,
                    name: "GROUP_1",
                    authorities: ['ROLE_1'],
            )
            securityService.createGroup(
                    tenantId: tenantId,
                    name: "GROUP_2",
                    authorities: ['ROLE_2'],
            )
            securityService.createGroup(
                    tenantId: tenantId,
                    name: "GROUP_3",
                    authorities: ['ROLE_3'],
            )

            securityService.createUser(
                    tenantId: tenantId,
                    username: tenantId == tenantService.defaultTenantId ? 'user1' : "${tenantId}/user1",
                    password: 'user1',
                    groups: ['GROUP_1'],
                    email: 'user@company.it',
                    firstname: 'User',
                    lastname: 'ONE',
            )
            securityService.createUser(
                    tenantId: tenantId,
                    username: tenantId == tenantService.defaultTenantId ? 'user2' : "${tenantId}/user2",
                    password: 'user2',
                    groups: ['GROUP_2', 'GROUP_1'],
                    email: 'user@company.it',
                    firstname: 'User',
                    lastname: 'ONE TWO',
            )
            securityService.createUser(
                    tenantId: tenantId,
                    username: tenantId == tenantService.defaultTenantId ? 'user3' : "${tenantId}/user3",
                    password: 'user3',
                    groups: ['GROUP_3'],
                    email: 'user@company.it',
                    firstname: 'User',
                    lastname: 'THREE',
            )

            //securityService.createUser(
            //    username: 'testuser',
            //    firstname: 'LDAP',
            //    lastname: 'User',
            //)

            securityService.createSystemUser(
                    username: '*',
                    firstname: 'Nessun',
                    lastname: 'Utente',
            )
        }

        securityService.afterLogin { GrailsHttpSession session ->
            println "${tenantService.currentTenantId}: Benvenuto in ${shellService.shell.id} caro ${securityService.currentUsername}"
            if (systemPropertyService.getBoolean('TEST_DENY_LOGIN', true)) {
                securityService.denyLogin('Cannot execute login because of X reason')
            }
        }

        securityService.afterLogout {
            println "Arrivederci ${securityService.currentUsername}!"
        }

        applicationService.init {
            applicationService.registerPrettyPrinter(TPerson, '${it.name}')
            applicationService.registerPrettyPrinter(TCompany, '${it.name}')
            applicationService.registerPrettyPrinter('customCompanyPrinter', '${it.name} (${it.dateCreated})')

            transitionService.subscribe('macchina-1')
            transitionService.subscribe('macchina-2')


            systemPropertyService.onChange('TEST_ON_CHANGE') { Object oldValue, Object value, Object defaultValue ->
                println "*** TEST_ON_CHANGE *************************************************************"
                println "OLD VALUE: $oldValue"
                println "VALUE: $value"
                println "DEFAULT VALUE: $defaultValue"
                println "********************************************************************************"
            }

            // APPLICATION CONFIGURATION
            //
            applicationService.registerCredits('Created by', 'Gianluca Sartori', 'Francesco Piceghello')
            applicationService.registerCredits('Tested by', 'Gianluca Sartori', 'Francesco Piceghello')
            applicationService.registerCredits('Sponsored by', 'Gianluca Sartori', 'Francesco Piceghello')
            applicationService.registerCredits('Dreamed by', 'Gianluca Sartori', 'Francesco Piceghello')

            //
            // FEATURES
            //

            applicationService.registerUserFeature(
                    controller: 'sandbox',
                    icon: 'fa-book',
                    text: 'Manual...',
            )

            applicationService.registerFeature(
                    controller: 'crud',
                    icon: 'fa-file',
                    favourite: true,
            )
            applicationService.registerFeature(
                    controller: 'crudCustom',
                    icon: 'fa-regular fa-file',
            )
            applicationService.registerFeature(
                    controller: 'crudDataServices',
                    icon: 'fa-file-circle-check',
            )
            applicationService.registerFeature(
                    controller: 'crudSql',
                    icon: 'fa-database',
            )
//            applicationService.registerFeature(
//                    controller: 'crudRest',
//                    icon: 'fa-cloud',
//                    authorities: ['ROLE_USER']
//            )

            applicationService.registerFeature(
                    controller: 'elements',
                    favourite: true,
            )
            applicationService.registerFeature(
                    parent: 'elements',
                    controller: 'sandbox',
                    icon: 'fa-umbrella-beach',
                    favourite: true,
            )
            applicationService.registerFeature(
                    parent: 'elements',
                    controller: 'table',
                    icon: 'fa-table',
                    favourite: true,
            )
            applicationService.registerFeature(
                    parent: 'elements',
                    controller: 'tableStressTest',
                    icon: 'fa-table',
            )
            applicationService.registerFeature(
                    parent: 'elements',
                    controller: 'form',
                    icon: 'fa-check-square',
                    favourite: true,
            )
            applicationService.registerFeature(
                    parent: 'elements',
                    controller: 'select',
                    icon: 'fa-table-list',
                    favourite: true,
            )
            applicationService.registerFeature(
                    parent: 'elements',
                    controller: 'grid',
                    icon: 'fa-border-none',
            )
            applicationService.registerFeature(
                    parent: 'elements',
                    controller: 'tabs',
                    icon: 'fa-ellipsis-h',
            )
            applicationService.registerFeature(
                    parent: 'elements',
                    controller: 'transitions',
                    icon: 'fa-wand-sparkles',
                    modal: true,
                    favourite: true,
            )
            applicationService.registerFeature(
                    parent: 'elements',
                    controller: 'websocket',
                    icon: 'fa-envelope',
                    favourite: true,
            )
            applicationService.registerFeature(
                    parent: 'elements',
                    controller: 'charts',
                    icon: 'fa-chart-pie',
                    favourite: true,
            )
            applicationService.registerFeature(
                    parent: 'elements',
                    controller: 'upload',
                    icon: 'fa-upload',
                    favourite: true,
            )
            applicationService.registerFeature(
                    parent: 'elements',
                    controller: 'staticResources',
                    icon: 'fa-images',
            )
            applicationService.registerFeature(
                    parent: 'elements',
                    controller: 'customPage',
                    icon: 'fa-palette',
                    direct: true,
            )

            applicationService.registerFeature(
                    controller: 'errorMessages',
            )
            applicationService.registerFeature(
                    parent: 'errorMessages',
                    controller: 'handledException',
                    icon: 'fa-regular fa-comment',
            )
            applicationService.registerFeature(
                    parent: 'errorMessages',
                    controller: 'denied',
                    icon: 'fa-user-lock',
            )
            applicationService.registerFeature(
                    parent: 'errorMessages',
                    controller: 'notFound',
                    icon: 'fa-search-location',
            )
            applicationService.registerFeature(
                    parent: 'errorMessages',
                    controller: 'exception',
                    icon: 'fa-solid fa-bug',
            )

            applicationService.registerFeature(
                    controller: 'authDemo',
                    icon: 'fa-group',
                    favourite: true,
                    authorities: ['ROLE_1', 'ROLE_2', 'ROLE_3']
            )
            applicationService.registerFeature(
                    parent: 'authDemo',
                    controller: 'user1',
                    icon: 'fa-users',
                    favourite: true,
                    authorities: ['ROLE_1']
            )
            applicationService.registerFeature(
                    parent: 'authDemo',
                    controller: 'user2',
                    icon: 'fa-users',
                    favourite: true,
                    authorities: ['ROLE_2']
            )
            applicationService.registerFeature(
                    parent: 'authDemo',
                    controller: 'user3',
                    icon: 'fa-users',
                    favourite: true,
                    authorities: ['ROLE_3']
            )
        }
    }

    def destroy = {
        //no-op
    }

}
