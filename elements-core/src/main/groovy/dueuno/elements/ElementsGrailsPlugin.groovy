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
package dueuno.elements

import dueuno.elements.core.SessionInitializer
import dueuno.elements.tenants.TenantForCurrentUserResolver
import grails.plugins.Plugin
import groovy.util.logging.Slf4j
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * @author Gianluca Sartori
 */

@Slf4j
@EnableScheduling
class ElementsGrailsPlugin extends Plugin {

    static final String NAME = 'elements-core'

    def version = '2.6.17'
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = '6.2.0 > *'
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        'grails-app/views/error.gsp'
    ]

    def title = 'Elements' // Headline display name of the plugin
    def author = 'Gianluca Sartori, Francesco Piceghello'
    def authorEmail = 'g.sartori@gmail.com, f.piceghello@gmail.com'
    def description = 'Elements'
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = 'https://www.dueuno.com/docs'

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = 'APACHE'

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: 'My Company', url: 'http://www.my-company.com/' ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: 'Joe Bloggs', email: 'joe@bloggs.net' ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: 'JIRA', url: 'http://jira.grails.org/browse/GPMYPLUGIN' ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: 'http://svn.codehaus.org/grails-plugins/' ]

    Closure doWithSpring() { {->
        // See: https://github.com/grails/grails-core/issues/3164
        xmlns context: 'http://www.springframework.org/schema/context'
        context.'component-scan' 'base-package': 'dueuno'

        tenantForCurrentUserResolver(TenantForCurrentUserResolver)
        sessionInitializer(SessionInitializer)
//        keyChain(KeyChain)
    } }

    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }

    void doWithApplicationContext() {
        // TODO Implement post initialization spring config (optional)
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
