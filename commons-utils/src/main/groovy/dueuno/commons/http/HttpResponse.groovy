package dueuno.commons.http

import groovy.transform.CompileStatic
import org.apache.hc.core5.http.Header

@CompileStatic
class HttpResponse {

    final Integer status
    final String message
    final List<Header> headers
    final Object body

    private HttpResponse(Integer status, String message, Header[] headers, Object body) {
        this.status = status
        this.message = message
        this.headers = headers as List<Header> ?: []
        this.body = body
    }

    static HttpResponse success(Integer status, Header[] headers, Object body) {
        return new HttpResponse(status, '', headers, body)
    }

    static HttpResponse error(Integer status, String message, Header[] headers, String body) {
        return new HttpResponse(status, message, headers, body)
    }

    Header hasHeader(String name) {
        return headers.find { it.name.equalsIgnoreCase(name) }
    }

    List<Header> getHeaders() {
        return headers
    }

    boolean isOk() {
        return status >= 200 && status < 300
    }

    boolean isError() {
        return status >= 400
    }

}
