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
package dueuno.elements.style

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
class Color {
    static List<Integer> hexToIntColor(String hexRgbColor) {
        if (!hexRgbColor) return []
        if (!hexRgbColor.startsWith('#') || hexRgbColor.size() != 7) {
            log.warn "Color '${hexRgbColor}' is not a valid RGB color."
            return []
        }

        String hexColor = hexRgbColor.replaceAll('#', '')
        List<Integer> intColor = []

        for (int i = 0; i < 3; i++) {
            String hexC = hexColor[i * 2..i * 2 + 1]
            Integer intC = Integer.parseInt(hexC, 16)
            intColor.add(intC)
        }

        return intColor
    }

}

@CompileStatic
enum TextAlign {
    DEFAULT(''),
    START('text-start'),
    END('text-end'),
    CENTER('text-center')

    final String cssClass

    TextAlign(String cssClass) {
        this.cssClass = cssClass
    }

    String toString() {
        return cssClass
    }
}

@CompileStatic
enum VerticalAlign {
    DEFAULT(''),
    TOP('align-top'),
    BOTTOM('align-bottom'),
    CENTER('align-center')

    final String cssClass

    VerticalAlign(String cssClass) {
        this.cssClass = cssClass
    }

    String toString() {
        return cssClass
    }
}

@CompileStatic
enum TextWrap {
    DEFAULT(''),
    NO_WRAP('no-wrap'),
    SOFT_WRAP('soft-wrap'),
    LINE_WRAP('line-wrap'),
    LINE_BREAK('line-break')

    final String cssClass

    TextWrap(String cssClass) {
        this.cssClass = cssClass
    }

    String toString() {
        return cssClass
    }
}

@CompileStatic
enum TextTransform {
    NONE(''),
    UPPERCASE('uppercase'),
    LOWERCASE('lowercase'),
    CAPITALIZE('capitalize')

    final String cssClass

    TextTransform(String cssClass) {
        this.cssClass = cssClass
    }

    String toString() {
        return cssClass
    }
}
