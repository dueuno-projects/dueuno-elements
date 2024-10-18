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

@CompileStatic
enum TextStyle {
    NORMAL(''),
    BOLD('fw-bold'),
    LIGHT('fw-light'),
    ITALIC('fst-italic'),
    MONOSPACE('font-monospace'),
    UNDERLINE('text-decoration-underline'),
    LINE_THROUGH('text-decoration-line-through')

    final String textStyle

    TextStyle(String textStyle) {
        this.textStyle = textStyle ?: ''
    }

    String toString() {
        return textStyle
    }
}