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
package dueuno.elements.utils

import grails.util.Environment
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
class EnvUtils {

    /**
     * Returns true if the the app is running in grails DEV environment
     * @return true if the the app is running in grails DEV environment
     */
    static Boolean isDevelopment() {
        return currentEnvironment == 'development'
    }

    /**
     * Returns true if the the app is running in grails TEST environment
     * @return true if the the app is running in grails TEST environment
     */
    static Boolean isTesting() {
        return currentEnvironment == 'test'
    }

    /**
     * Returns true if the the app is running in grails PROD environment
     * @return true if the the app is running in grails PROD environment
     */
    static Boolean isProduction() {
        return currentEnvironment == 'production'
    }

    /**
     * Returns a string specifying the currently running environment: 'dev', 'test' or 'prod'
     * @return a string specifying the currently running environment: 'dev', 'test' or 'prod'
     */
    static String getCurrentEnvironment() {
        return Environment.currentEnvironment.name
    }
}
