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

import io.micronaut.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.ServletContextInitializer

import javax.servlet.ServletContext
import javax.servlet.ServletException

/**
 * @author Gianluca Sartori
 */

@Bean
class SessionInitializer implements ServletContextInitializer {

    @Autowired
    ApplicationService applicationService

    @Override
    void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.sessionCookieConfig.name = applicationService.applicationName.toUpperCase() + '-SESSION'
    }
}
