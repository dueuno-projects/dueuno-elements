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
package dueuno.elements.exceptions

import dueuno.elements.core.PrettyPrinter
import dueuno.elements.core.WebRequestAware
import groovy.transform.CompileStatic
import org.springframework.validation.ObjectError

/**
 * Main exception to throw to users. Returns and i18n exception when available
 *
 * @author Gianluca Sartori
 */
@CompileStatic
class ElementsException extends Exception implements WebRequestAware {

    private Object[] args

    ElementsException(ObjectError error) {
        super(error.code)
        this.args = error.arguments
    }

    ElementsException(String message, List args = []) {
        super(message)
        this.args = args as Object[]
    }

    ElementsException(String message, Object[] args) {
        super(message)
        this.args = args
    }

    ElementsException(String message, Throwable cause) {
        super(message, cause)
        this.args = []
    }

    @Override
    String getMessage() {
        if (hasRequest()) {
            return PrettyPrinter.message(locale, super.message, args)
        }

        return super.message
    }
}