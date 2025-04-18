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

    static final String ERROR_TEXT = '#cc0000'
    static final String ERROR_BACKGROUND = '#ffdddd'
    static final String WARNING_TEXT = '#755e01'
    static final String WARNING_BACKGROUND = '#ffeeaa'
    static final String SUCCESS_TEXT = '#014513'
    static final String SUCCESS_BACKGROUND = '#bce3c6'
    static final String DISABLED_TEXT = '#777777'

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