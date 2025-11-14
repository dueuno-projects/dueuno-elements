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
 * Represents an HTTP request to be executed by {@link HttpClient}.
 * <p>
 * This class provides a fluent API for building HTTP requests with:
 * <ul>
 *     <li>HTTP method (GET, POST, PUT, DELETE, PATCH)</li>
 *     <li>Absolute URL validation</li>
 *     <li>Headers and query parameters</li>
 *     <li>Request body (plain, JSON, or multipart)</li>
 * </ul>
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * def request = HttpRequest.post("https://api.example.com/v1/users")
 *     .header("Authorization", "Bearer token")
 *     .body([name: "Alice"])
 * }</pre>
 * </p>
 *
 * @author Gianluca Sartori
 */
@CompileStatic
class HttpRequest {

    /**
     * The HTTP method of the request (GET, POST, PUT, DELETE, PATCH).
     */
    final HttpMethod method

    /**
     * The absolute URL of the request.
     */
    String url

    /**
     * Map of HTTP headers for the request.
     */
    final Map<String, String> headers = [:]

    /**
     * Map of query parameters for the request.
     */
    final Map<String, Object> query = [:]

    /**
     * The body content of the request. Can be a String, JSON object, or HttpEntity.
     */
    Object body

    /**
     * Private constructor. Use static factory methods to create instances.
     *
     * @param method the HTTP method for the request
     * @param url the absolute URL of the request
     */
    private HttpRequest(HttpMethod method, String url) {
        this.method = method
        setUrl(url)
    }

    /**
     * Sets and validates the absolute URL for this request.
     * Accepts only valid HTTP or HTTPS URLs.
     *
     * @param url the absolute URL
     * @return this HttpRequest instance for chaining
     * @throws IllegalArgumentException if the URL is null, empty, malformed, or uses a non-HTTP/HTTPS scheme
     */
    HttpRequest setUrl(String url) {
        if (!url) {
            throw new IllegalArgumentException("URL cannot be null or empty")
        }

        try {
            def uri = new URI(url)
            if (!["http", "https"].contains(uri.scheme?.toLowerCase())) {
                throw new IllegalArgumentException("Invalid URL scheme: ${uri.scheme}. Only HTTP/HTTPS are allowed.")
            }
            // Optional: Normalize (remove trailing ? or /)
            this.url = uri.toString()
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL format: ${url}", e)
        }

        return this
    }

    /**
     * Creates a GET request with the specified URL.
     *
     * @param url the absolute URL
     * @return a new HttpRequest instance with method GET
     */
    static HttpRequest GET(String url)    { new HttpRequest(HttpMethod.GET, url) }

    /**
     * Creates a POST request with the specified URL.
     *
     * @param url the absolute URL
     * @return a new HttpRequest instance with method POST
     */
    static HttpRequest POST(String url)   { new HttpRequest(HttpMethod.POST, url) }

    /**
     * Creates a PUT request with the specified URL.
     *
     * @param url the absolute URL
     * @return a new HttpRequest instance with method PUT
     */
    static HttpRequest PUT(String url)    { new HttpRequest(HttpMethod.PUT, url) }

    /**
     * Creates a DELETE request with the specified URL.
     *
     * @param url the absolute URL
     * @return a new HttpRequest instance with method DELETE
     */
    static HttpRequest DELETE(String url) { new HttpRequest(HttpMethod.DELETE, url) }

    /**
     * Creates a PATCH request with the specified URL.
     *
     * @param url the absolute URL
     * @return a new HttpRequest instance with method PATCH
     */
    static HttpRequest PATCH(String url)  { new HttpRequest(HttpMethod.PATCH, url) }

    /**
     * Adds or replaces a single HTTP header.
     *
     * @param name the header name
     * @param value the header value
     * @return this HttpRequest instance for chaining
     */
    HttpRequest header(String name, String value) {
        if (value != null) headers[name] = value
        return this
    }

    /**
     * Adds multiple HTTP headers at once.
     *
     * @param values a map of header names to values
     * @return this HttpRequest instance for chaining
     */
    HttpRequest headers(Map<String, String> values) {
        if (values) headers.putAll(values)
        return this
    }

    /**
     * Adds a single query parameter.
     *
     * @param name the parameter name
     * @param value the parameter value
     * @return this HttpRequest instance for chaining
     */
    HttpRequest query(String name, Object value) {
        if (value != null) query[name] = value
        return this
    }

    /**
     * Adds multiple query parameters at once.
     *
     * @param params a map of parameter names to values
     * @return this HttpRequest instance for chaining
     */
    HttpRequest query(Map<String, Object> params) {
        if (params) query.putAll(params)
        return this
    }

    /**
     * Sets the body of the request.
     *
     * @param body the request body, can be a String, JSON object, or HttpEntity
     * @return this HttpRequest instance for chaining
     */
    HttpRequest body(Object body) {
        this.body = body
        return this
    }

    /**
     * Sets a multipart body for the request.
     *
     * @param multipart the {@link HttpMultipartBody} instance
     * @return this HttpRequest instance for chaining
     */
    HttpRequest multipartBody(HttpMultipartBody multipart) {
        this.body = multipart.build()
        return this
    }

    /**
     * Builds the final URL string including encoded query parameters.
     *
     * @return the full URL with query parameters
     */
    String buildUrl() {
        if (query.isEmpty()) return url

        String queryString = query.collect { k, v ->
            "${URLEncoder.encode(k, 'UTF-8')}=${URLEncoder.encode(v.toString(), 'UTF-8')}"
        }.join("&")

        return url.contains("?") ? "${url}&${queryString}" : "${url}?${queryString}"
    }

    /**
     * Returns a string representation of the HTTP request,
     * including method, URL, headers, and query parameters.
     *
     * @return string representation of the request
     */
    @Override
    String toString() {
        return "${method} ${url} headers=${headers.keySet()} query=${query}"
    }
}
