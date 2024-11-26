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
package dueuno

import dueuno.elements.core.ApplicationService

class BootStrap {

    ApplicationService applicationService

    def init = { servletContext ->
        applicationService.init {

            registerPrettyPrinter('BOOK', '${it.title} - ${it.author}')

            registerFeature(
                    controller: 'movie',
                    icon: 'fa-film',
                    favourite: true,
            )
            registerFeature(
                    controller: 'book',
                    icon: 'fa-book',
            )
            registerFeature(
                    controller: 'read',
                    icon: 'fa-glasses',
            )
        }
    }

    def destroy = {
    }
}