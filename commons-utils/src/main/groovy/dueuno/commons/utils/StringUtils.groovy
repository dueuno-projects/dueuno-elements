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

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.security.MessageDigest

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
class StringUtils {

    // kebab-case
    static String camelToKebab(String s) {
        def r = ''; for (c in s) {
            if (c in "A".."Z") {
                r += "-${c.toLowerCase()}"
            } else {
                r += c
            }
        }; r
    }

    static String kebabToCamel(String s) {
        def p = false; def r = '';
        for (c in s) {
            if (c == '-') {
                p = true; continue
            } else {
                r += p ? c.toUpperCase() : c.toLowerCase(); p = false
            }
        }; r
    }

    // snake_case
    static String camelToSnake(String s) {
        def r = ''; for (c in s) {
            if (c in "A".."Z") {
                r += "_${c.toLowerCase()}"
            } else {
                r += c
            }
        }; r.drop(1)
    }

    static String snakeToCamel(String s) {
        def p = false; def r = '';
        for (c in s) {
            if (c == '_') {
                p = true; continue
            } else {
                r += p ? c.toUpperCase() : c.toLowerCase(); p = false
            }
        }; r
    }

    // SCREAMING_SNAKE_CASE
    static String camelToScreamingSnake(String s) {
        def r = ''; for (c in s) {
            if (c in "A".."Z") {
                r += "_${c.toUpperCase()}"
            } else {
                r += c.toUpperCase()
            }
        }; r
    }

    static String screamingSnakeToCamel(String s) {
        snakeToCamel(s)
    }

    // PascalCase
    static String pascalToCamel(String s) {
        return s.uncapitalize()
    }

    static String camelToPascal(String s) {
        return s.capitalize()
    }

    //
    // Utils
    //
    static String removeNonPrintableChars(String s) {
        return s.replaceAll('\\P{Print}', '')
    }

    static String truncateIfLonger(String s, Integer size) {
        return s.size() > size ? s.substring(0, size) : s
    }

    static Boolean isNumeric(String value) {
        for (c in value) {
            if ('1234567890'.indexOf(c) == -1) {
                return false
            }
        }
        return true
    }

    static String generateRandomToken(Integer size = 16, List alphabet = []) {
        List theAlphabet = alphabet ?: ('A'..'Z') + ('0'..'9') + ('a'..'z')

        return new Random().with {
            (1..size).collect { theAlphabet[nextInt(theAlphabet.size())] }.join('')
        }
    }

    static String generateHash(String input) {
        MessageDigest md5 = MessageDigest.getInstance('MD5')
        md5.update(input.getBytes())
        BigInteger hash = new BigInteger(1, md5.digest())
        return hash.toString(16)
    }

    static Map parseVariablesString(String variablesString) {
        if (!variablesString) {
            return [:]
        }

        List variableList = variablesString.tokenize(';')
        Map result = [:]

        for (var in variableList) {
            List<String> pair = var.split('=') as List<String>
            if (pair.size() > 0) {
                String key = pair[0].trim()
                String value = pair[1]?.trim()
                result.put(key, value)
            }
        }

        return result
    }

    static String inputStreamToString(InputStream inputStream) {
        ByteArrayOutputStream result = new ByteArrayOutputStream()
        byte[] buffer = new byte[1024]
        for (int length; (length = inputStream.read(buffer)) != -1; ) {
            result.write(buffer, 0, length)
        }
        // StandardCharsets.UTF_8.name() > JDK 7
        return result.toString("UTF-8")
    }

    static String base64Encode(String inputString) {
        String encoded = inputString.bytes.encodeBase64().toString()
        return encoded
    }

    static String base64Decode(String encodedString) {
        byte[] decoded = encodedString.decodeBase64()
        String decode = new String(decoded)
        return decode
    }
}
