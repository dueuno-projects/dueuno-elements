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

import dueuno.elements.utils.EnvUtils
import grails.core.GrailsApplication
import grails.plugins.GrailsPluginManager
import groovy.transform.CompileStatic
import jakarta.servlet.ServletContext
import org.springframework.beans.factory.annotation.Autowired

import java.lang.management.ManagementFactory

/**
 * @author Gianluca Sartori
 */

@CompileStatic
class SystemInfoService implements WebRequestAware {

    @Autowired
    GrailsPluginManager grailsPluginManager

    GrailsApplication grailsApplication
    ServletContext servletContext

    /**
     * INTERNAL USE ONLY. Returns a map with the system info.
     */
    Map getInfo() {
        Double freeMemory = Runtime.getRuntime().freeMemory() / Math.pow(1024, 2)
        Double maxMemory = Runtime.getRuntime().maxMemory() / Math.pow(1024, 2)
        Double usedMemory = (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory()) / Math.pow(1024, 2)
        String gcNames = ManagementFactory.getGarbageCollectorMXBeans().collect { it.name }.join(", ")

        return [
                environment  : message('default.env.' + EnvUtils.currentEnvironment),
                browser      : request.getHeader("User-Agent"),

                appName      : grailsApplication.config.getProperty('info.app.name', String) as String,
                appVersion   : grailsApplication.config.getProperty('info.app.version', String) as String,
                appPath      : servletContext.getRealPath('/'),

                dueunoVersion: elementsVersion,
                grailsVersion: grailsApplication.config.getProperty('info.app.grailsVersion', String) as String,
                groovyVersion: GroovySystem.getVersion(),
                serverVersion: servletContext.getServerInfo(),

                jvmHeapMax   : "${Math.round(maxMemory)} Mb",
                jvmHeapUsed  : "${Math.round(usedMemory)} Mb",
                jvmHeapFree  : "${Math.round(freeMemory)} Mb",
                jvmGC        : gcNames,
                jvmPath      : System.getProperty('java.home'),
                jvmVersion   : System.getProperty('java.version') + ' ' + System.getProperty('java.vendor'),

                osVersion    : System.getProperty('os.name') + ' ' + System.getProperty('os.version') + ' (' + System.getProperty('os.arch') + ')',
        ]
    }

    String getElementsVersion() {
        return (grailsPluginManager.allPlugins.find { it.name == 'elements' })?.version
    }

}