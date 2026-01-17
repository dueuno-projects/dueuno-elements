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
package dueuno.commons.http.client

/**
 * Defines the type of content returned by an HttpResponse.
 * <p>
 * This enumeration is used by {@link HttpClient#call} to determine
 * how the response body should be parsed or represented. It provides three
 * response types:
 * </p>
 *
 * <ul>
 *   <li><b>RAW</b> – The response body is returned as a plain {@link String}.</li>
 *   <li><b>JSON</b> – The response body is parsed into a {@link java.util.Map}.</li>
 *   <li><b>BYTES</b> – The response body is returned as a {@code byte[]} array.</li>
 * </ul>
 *
 * @author Gianluca Sartori
 */
enum HttpResponseType {

    /**
     * Indicates that the HttpResponse body should be returned as a raw {@link String}.
     */
    RAW,

    /**
     * Indicates that the HttpResponse body should be parsed and returned as a
     * {@link java.util.Map} representing a JSON object.
     */
    JSON,

    /**
     * Indicates that the HttpResponse body should be returned as a raw
     * {@code byte[]} array.
     **/
    BYTES
}