package dueuno.commons.http

import groovy.transform.CompileStatic
import org.apache.hc.core5.http.Header
import org.apache.hc.core5.http.message.BasicHeader

@CompileStatic
class HttpRequest {

    final HttpMethod method
    String url

    private final List<Header> headers = []
    private final Map<String, Object> query = [:]
    private Object body

    private HttpRequest(HttpMethod method, String url) {
        this.method = method
        setUrl(url)
    }

    HttpRequest setUrl(String url) {
        if (!url) {
            throw new IllegalArgumentException("URL cannot be null or empty")
        }

        try {
            def uri = new URI(url)
            if (!["http", "https"].contains(uri.scheme?.toLowerCase())) {
                throw new IllegalArgumentException("Invalid URL scheme: ${uri.scheme}. Only HTTP/HTTPS are allowed.")
            }
            this.url = uri.toString()

        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL format: ${url}")
        }

        return this
    }

    String getUrl() {
        if (query.isEmpty()) return this.url

        String queryString = query.collect { k, v ->
            "${URLEncoder.encode(k, 'UTF-8')}=${URLEncoder.encode(v.toString(), 'UTF-8')}"
        }.join("&")

        return this.url.contains("?") ? "${this.url}&${queryString}" : "${this.url}?${queryString}"
    }

    static HttpRequest GET(String url)    { new HttpRequest(HttpMethod.GET, url) }
    static HttpRequest POST(String url)   { new HttpRequest(HttpMethod.POST, url) }
    static HttpRequest PUT(String url)    { new HttpRequest(HttpMethod.PUT, url) }
    static HttpRequest DELETE(String url) { new HttpRequest(HttpMethod.DELETE, url) }
    static HttpRequest PATCH(String url)  { new HttpRequest(HttpMethod.PATCH, url) }

    HttpRequest header(String name, String value) {
        if (value != null) {
            headers << new BasicHeader(name, value)
        }

        return this
    }

    Header hasHeader(String name) {
        return headers.find { it.name.equalsIgnoreCase(name) }
    }

    List<Header> getHeaders() {
        return headers
    }

    HttpRequest query(String name, Object value) {
        if (value != null) {
            query[name] = value
        }

        return this
    }

    HttpRequest query(Map<String, Object> params) {
        if (params) {
            query.putAll(params)
        }

        return this
    }

    HttpRequest body(Object body) {
        this.body = body
        return this
    }

    HttpRequest multipartBody(HttpMultipartBody multipart) {
        this.body = multipart.build()
        return this
    }

    Object getBody() {
        return body
    }
}
