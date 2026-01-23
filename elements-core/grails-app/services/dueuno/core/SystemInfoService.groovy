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
package dueuno.core

import com.sun.management.OperatingSystemMXBean
import dueuno.utils.EnvUtils
import grails.core.GrailsApplication
import grails.plugins.GrailsPluginManager
import groovy.transform.CompileStatic
import jakarta.servlet.ServletContext
import org.springframework.beans.factory.annotation.Autowired

import java.lang.management.ManagementFactory
import java.time.Duration

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
        OperatingSystemMXBean hw = ManagementFactory.operatingSystemMXBean as OperatingSystemMXBean
        File hwHD = File.listRoots()[0]
        String jvmGC = ManagementFactory.getGarbageCollectorMXBeans().collect { it.name }.join(", ")
        Double jvmHeapFree = Runtime.getRuntime().freeMemory() / Math.pow(1024, 2)
        Double jvmHeapMax = Runtime.getRuntime().maxMemory() / Math.pow(1024, 2)
        Double jvmHeapUsed = (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory()) / Math.pow(1024, 2)
        Duration uptime = Duration.ofMillis(ManagementFactory.runtimeMXBean.uptime)

        return [
                environment       : message('default.env.' + EnvUtils.currentEnvironment),
                browser           : request.getHeader("User-Agent"),

                applicationName   : grailsApplication.config.getProperty('info.app.name', String) as String,
                applicationPath   : servletContext.getRealPath('/'),
                applicationVersion: grailsApplication.config.getProperty('info.app.version', String) as String,

                dueunoVersion     : elementsVersion,
                grailsVersion     : grailsApplication.config.getProperty('info.app.grailsVersion', String) as String,
                groovyVersion     : GroovySystem.getVersion(),
                serverVersion     : servletContext.getServerInfo(),

                jvmUptime         : "${uptime.toDays()}d ${uptime.toHoursPart()}h ${uptime.toMinutesPart()}m ${uptime.toSecondsPart()}s",
                jvmHeapMax        : "${Math.round(jvmHeapMax)} MB",
                jvmHeapUsed       : "${Math.round(jvmHeapUsed)} MB",
                jvmHeapFree       : "${Math.round(jvmHeapFree)} MB",
                jvmGC             : jvmGC,
                jvmPath           : System.getProperty('java.home'),
                jvmVersion        : System.getProperty('java.version') + ' ' + System.getProperty('java.vendor'),

                osVersion         : System.getProperty('os.name') + ' ' + System.getProperty('os.version'),

                hardwareHD        : "${(hwHD.totalSpace / 1_000_000_000).round(0)} GB (${(hwHD.totalSpace / (1024**3)).round(0)} GiB)",
                hardwareRAM       : "${(hw.totalMemorySize / (1024**3))} GB",
                hardwareCPU       : "${hw.availableProcessors} Cores (${hw.arch})",
        ]
    }

    String getElementsVersion() {
        return (grailsPluginManager.allPlugins.find { it.name == 'elements' })?.version
    }

}