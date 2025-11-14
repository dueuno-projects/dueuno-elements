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

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import org.apache.hc.client5.http.classic.methods.*
import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder
import org.apache.hc.client5.http.ssl.TrustAllStrategy
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.HttpEntity
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity
import org.apache.hc.core5.ssl.SSLContextBuilder
import org.apache.hc.core5.util.Timeout

import javax.net.ssl.SSLContext

/**
 * Utility class for executing HTTP requests using Apache HttpClient 5.
 * <p>
 * Provides static methods for performing standard REST operations (GET, POST, PUT, PATCH, DELETE)
 * with support for JSON payloads, plain text, and multipart bodies.
 * <p>
 * This class allows configuring connection and response timeouts, custom headers
 * (e.g., Authorization tokens), and automatic JSON serialization/deserialization.
 * </p>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * def client = HttpClient.create(30)
 * def headers = ['Authorization': 'Bearer myToken']
 * def response = HttpClient.callAsJson(client, "https://api.example.com/data", HttpRequest.get("/endpoint").headers(headers))
 *}</pre>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Configurable connection, request, and response timeouts</li>
 *   <li>Automatic JSON serialization/deserialization for request and response bodies</li>
 *   <li>Support for custom headers including Authorization tokens</li>
 *   <li>Exception handling for non-2xx HTTP responses</li>
 * </ul>
 *
 * @author Gianluca Sartori
 */
@CompileStatic
class HttpClient {

    /**
     * Creates a reusable {@link CloseableHttpClient} with configurable timeouts.
     *
     * @param timeoutSeconds Timeout in seconds for connection request and response (default 30s)
     * @param forceCertificateVerification If true, enforces SSL certificate validation; if false, trusts all certificates
     * @return A configured {@link CloseableHttpClient} instance
     */
    static CloseableHttpClient create(Integer timeoutSeconds = 30, Boolean forceCertificateVerification = false) {
        return forceCertificateVerification
                ? createValidCertHttpClient(timeoutSeconds)
                : createNoCertHttpClient(timeoutSeconds)
    }

    /**
     * Creates a {@link CloseableHttpClient} with SSL certificate verification enabled.
     *
     * @param timeoutSeconds Timeout in seconds for connection request and response
     * @return A {@link CloseableHttpClient} instance
     */
    private static CloseableHttpClient createValidCertHttpClient(Integer timeoutSeconds) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(timeoutSeconds))
                .setResponseTimeout(Timeout.ofSeconds(timeoutSeconds))
                .build()

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build()
    }

    /**
     * Creates a {@link CloseableHttpClient} that disables SSL certificate verification and hostname checks.
     * Useful for testing against servers with self-signed certificates.
     *
     * @param timeoutSeconds Timeout in seconds for connection request and response
     * @return A {@link CloseableHttpClient} instance
     */
    private static CloseableHttpClient createNoCertHttpClient(Integer timeoutSeconds) {
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                .build()

        SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext)
                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build()

        PoolingHttpClientConnectionManager connManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build()

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(timeoutSeconds))
                .setResponseTimeout(Timeout.ofSeconds(timeoutSeconds))
                .build()

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connManager)
                .build()

        return httpClient
    }

    /**
     * Builds an {@link HttpUriRequestBase} from a {@link HttpRequest} object,
     * setting headers, query parameters, and body.
     * <p>
     * Automatically handles JSON serialization and content type defaults for String or object bodies.
     * </p>
     *
     * @param request The {@link HttpRequest} containing method, URL, headers, query, and body
     * @return Configured {@link HttpUriRequestBase} instance
     * @throws Exception if the body object cannot be serialized to JSON
     */
    private static HttpUriRequestBase buildHttpRequest(HttpRequest request) {
        String url = request.buildUrl()

        HttpUriRequestBase httpRequest
        switch (request.method) {
            case HttpMethod.GET: httpRequest = new HttpGet(url); break
            case HttpMethod.POST: httpRequest = new HttpPost(url); break
            case HttpMethod.PUT: httpRequest = new HttpPut(url); break
            case HttpMethod.PATCH: httpRequest = new HttpPatch(url); break
            case HttpMethod.DELETE: httpRequest = new HttpDelete(url); break
            default: throw new IllegalArgumentException("Unsupported HTTP method: ${request.method}")
        }

        request.headers.each { k, v -> httpRequest.setHeader(k, v) }

        if (request.body == null) {
            return httpRequest
        }

        switch (request.body) {
            case HttpEntity:
                httpRequest.entity = request.body as HttpEntity
                break

            case String:
                ContentType contentType = ContentType.parse(request.headers["Content-Type"])
                httpRequest.entity = new StringEntity(request.body as String, contentType)

                if (!request.headers["Accept"]) {
                    httpRequest.setHeader("Accept", ContentType.TEXT_PLAIN.mimeType)
                }
                if (!request.headers["Content-Type"]) {
                    httpRequest.setHeader("Content-Type", ContentType.TEXT_PLAIN.mimeType)
                }
                break

            default:
                try {
                    String jsonBody = JsonOutput.toJson(request.body)
                    ContentType contentType = ContentType.parse(request.headers["Content-Type"])
                    httpRequest.entity = new StringEntity(jsonBody, contentType)

                    if (!request.headers["Accept"]) {
                        httpRequest.setHeader("Accept", ContentType.APPLICATION_JSON.mimeType)
                    }
                    if (!request.headers["Content-Type"]) {
                        httpRequest.setHeader("Content-Type", ContentType.APPLICATION_JSON.mimeType)
                    }

                } catch (Exception e) {
                    throw new Exception("Body of type '${request.body.getClass()}' is not supported: ${e.message ?: e.cause.message}")
                }
        }

        return httpRequest
    }

    /**
     * Executes the HTTP request and returns the response body as a String.
     *
     * @param client The {@link CloseableHttpClient} to execute the request
     * @param request The {@link HttpRequest} object
     * @return Response body as String
     * @throws Exception if the request fails
     */
    static String call(CloseableHttpClient client, HttpRequest request) {
        HttpUriRequestBase httpRequest = buildHttpRequest(request)

        return client.execute(httpRequest) { response ->
            String bodyText = response.entity ? EntityUtils.toString(response.entity) : ""
            return bodyText
        }
    }

    /**
     * Executes the HTTP request and parses the JSON response into a Map.
     *
     * @param client The {@link CloseableHttpClient} to execute the request
     * @param request The {@link HttpRequest} object
     * @return Parsed JSON as Map, or null if the response body is empty
     * @throws Exception if the response is not valid JSON
     */
    static Map callAsMap(CloseableHttpClient client, HttpRequest request) {
        String responseText = call(client, request)
        if (!responseText) {
            return null
        }

        try {
            Object parsed = new JsonSlurper().parseText(responseText)
            if (parsed in Map) {
                return parsed as Map
            } else {
                throw new Exception("Invalid JSON response.\nBody: ${responseText}")
            }

        } catch (Exception e) {
            throw new Exception("Invalid JSON response: ${e.message}\nBody: ${responseText}")
        }
    }

    /**
     * Executes the HTTP request and returns the response as a byte array.
     *
     * @param client The {@link CloseableHttpClient} to execute the request
     * @param request The {@link HttpRequest} object
     * @return Response body as byte array
     * @throws Exception if the HTTP status is not in the 2xx range
     */
    static byte[] callAsBytes(CloseableHttpClient client, HttpRequest request) {
        HttpUriRequestBase httpRequest = buildHttpRequest(request)

        if (!request.headers.containsKey("Accept")) {
            httpRequest.setHeader("Accept", "*/*")
        }

        return client.execute(httpRequest) { response ->
            int status = response.code
            byte[] bytes = response.entity ? EntityUtils.toByteArray(response.entity) : new byte[0]

            if (status >= 200 && status < 300) {
                return bytes
            } else {
                String errorText = ""
                try {
                    errorText = new String(bytes)
                } catch (ignored) {
                }
                throw new Exception("HTTP Error: ${status} - ${errorText}")
            }
        }
    }
}
