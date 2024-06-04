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

import grails.plugin.springsecurity.annotation.Secured
import org.grails.exceptions.ExceptionUtils

/**
 * INTERNAL USE ONLY
 *
 * @author Gianluca Sartori
 */
@Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
class HttpStatusController {

    def err500() {
        withFormat {
            json {
                Map obj = [
                        error: ExceptionUtils.getRootCause(request.exception)
                ]
                respond obj
            }
            '*' {
                render view: '/dueuno/elements/httpStatus/500'
            }
        }
    }

    def err404() {
        withFormat {
            json {
                render status: 404
            }
            '*' {
                render view: '/dueuno/elements/httpStatus/404'
            }
        }
    }
}
