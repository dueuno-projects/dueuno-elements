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
package dueuno.commons.http

import groovy.transform.CompileStatic

/**
 * Represents the standard HTTP methods used in requests.
 *
 * @author Gianluca Sartori
 */
@CompileStatic
enum HttpMethod {

    /**
     * The GET method requests a representation of the specified resource.
     * Requests using GET should only retrieve data.
     */
    GET,

    /**
     * The POST method submits data to the specified resource, often causing
     * a change in state or side effects on the server.
     */
    POST,

    /**
     * The PUT method replaces all current representations of the target resource
     * with the uploaded content.
     */
    PUT,

    /**
     * The DELETE method deletes the specified resource.
     */
    DELETE,

    /**
     * The PATCH method applies partial modifications to a resource.
     */
    PATCH
}
