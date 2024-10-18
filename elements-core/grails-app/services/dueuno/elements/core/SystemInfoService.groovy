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

import grails.core.GrailsApplication
import grails.plugins.GrailsPluginManager
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils
import org.springframework.beans.factory.annotation.Autowired

import jakarta.servlet.ServletContext

/**
 * @author Gianluca Sartori
 */

class SystemInfoService {

    @Autowired
    private GrailsApplication grailsApplication

    @Autowired
    private ServletContext servletContext

    /**
     * INTERNAL USE ONLY. Returns a map with the system info.
     */
    Map getInfo() {
        GrailsPluginManager pluginManager = (GrailsPluginManager) Elements.getBean('pluginManager')
        GrailsWebRequest request = WebUtils.retrieveGrailsWebRequest()

        return [
                browser      : request.getHeader("User-Agent"),
                appName      : grailsApplication.config.getProperty('info.app.name', String),
                appVersion   : grailsApplication.config.getProperty('info.app.version', String),
                appPath      : servletContext.getRealPath('/'),
                deployedAsWAR: grailsApplication.isWarDeployed(),

                serverVersion: servletContext.getServerInfo(),
                grailsVersion: grailsApplication.config.getProperty('info.app.grailsVersion', String),
                groovyVersion: GroovySystem.getVersion(),

                jvmHeapMax   : "${Math.round(Runtime.getRuntime().maxMemory() / Math.pow(1024, 2))} Mb",
                jvmHeapUsed  : "${Math.round(Runtime.getRuntime().totalMemory() / Math.pow(1024, 2))} Mb",
                jvmHeapFree  : "${Math.round(Runtime.getRuntime().freeMemory() / Math.pow(1024, 2))} Mb",
                jvmVersion   : System.getProperty('java.version') + ' ' + System.getProperty('java.vendor'),
                jvmPath      : System.getProperty('java.home'),

                osName       : System.getProperty('os.name'),
                osVersion    : System.getProperty('os.version'),
                osArch       : System.getProperty('os.arch'),

                coreVersion  : (pluginManager.allPlugins.find { it.name == 'elements' })?.version,
        ]
    }
}