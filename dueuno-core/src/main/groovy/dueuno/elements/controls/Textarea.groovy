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
package dueuno.elements.controls

import dueuno.commons.utils.StringUtils
import dueuno.types.Type
import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */

@CompileStatic
class Textarea extends TextField {

    Boolean acceptNewLine
    Boolean encode

    Textarea(Map args) {
        super(args)

        valueType = Type.TEXT
        autoSelect = args.autoSelect == null ? false : args.autoSelect
        acceptNewLine = args.acceptNewLine == null ? true : args.acceptNewLine
        encode = args.encode == null ? false : args.encode

        containerSpecs.multiline = true
    }

    @Override
    String getPropertiesAsJSON(Map properties = [:]) {
        Map thisProperties = [
                autoSelect: autoSelect,
                acceptNewLine: acceptNewLine,
                encode: encode,
        ]
        return super.getPropertiesAsJSON(thisProperties + properties)
    }

    //
    // Utils
    //
    static String decodeText(String encodedString) {
        return StringUtils.base64Decode(encodedString)
    }
}
