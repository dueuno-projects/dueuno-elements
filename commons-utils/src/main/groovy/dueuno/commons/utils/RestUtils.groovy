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
package dueuno.commons.utils

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.Part

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
class RestUtils {

    private static final JsonSlurper JSON_SLURPER = new JsonSlurper()

    static Map toMap(String jsonString) {
        return JSON_SLURPER.parseText(jsonString) as Map
    }

    @CompileDynamic
    static Map get(String url) {
        URLConnection get = new URL(url).openConnection()
        Integer responseCode = get.responseCode

        Map result = [responsCode: responseCode]
        if (responseCode == 200) {
            String responseText = get.inputStream.text
            Map response = toMap(responseText)
            result.putAll(response)
        }
        return result
    }

    @CompileDynamic
    static Map post(String url, Map object) {
        URL u = new URL(url)
        URLConnection conn = u.openConnection()
        String requestText = JsonOutput.toJson(object)

        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/json")
        if (u.userInfo != null) {
            String basicAuth = "Basic " + new String(new Base64().encode(u.userInfo.getBytes()))
            conn.setRequestProperty("Authorization", basicAuth)
        }
        conn.outputStream.write(requestText.getBytes("UTF-8"))
        Integer responseCode = conn.responseCode

        Map result = [responsCode: responseCode]
        if (responseCode == 200) {
            String responseText = conn.inputStream.text
            Map response = toMap(responseText)
            result.putAll(response)
        }
        return result
    }

    static Map jsonPartToMap(HttpServletRequest request, String name) {
        Part jsonPart = request.getPart(name)
        String jsonString = StringUtils.inputStreamToString(jsonPart.inputStream)

        return toMap(jsonString)
    }

    static void saveFilePart(HttpServletRequest request, String name, String toPathname) {
        Part jsonPart = request.getPart(name)
        jsonPart.write(toPathname)
    }

    static Map requestBodyToMap(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder()
        BufferedReader reader = request.getReader()
        String line
        while ((line = reader.readLine()) != null) {
            sb.append(line)
        }
        String jsonString = sb.toString()

        return toMap(jsonString)
    }
}
