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
package dueuno.exceptions

import groovy.transform.CompileStatic

/**
 * Used to require named arguments
 *
 * @author Gianluca Sartori
 */
@CompileStatic
class ArgsException extends Exception {
    ArgsException(String message) {
        super(message)
    }

    static Object requireArgument(Map args, String requiredArgName) {
        Object requiredArg = getRequiredArgument(args, requiredArgName)
        if (requiredArg == null || requiredArg == '') {
            throw new ArgsException("The method requires the parameter '${requiredArgName}' but none was provided in the call.")
        } else {
            return requiredArg
        }
    }

    static Map requireArgument(Map args, List requiredArgNames, Boolean or = false) {
        Map results = [:]

        for (requiredArgName in requiredArgNames) {
            results[(String) requiredArgName] = getRequiredArgument(args, (String) requiredArgName)
        }

        if (or) {
            Boolean atLeastOneValuePassed = results.find { k, v -> v != null }
            if (atLeastOneValuePassed) {
                return results
            } else {
                String prettyArguments = requiredArgNames.join(', ')
                throw new ArgsException("The method requires at least one of the following parameters [${prettyArguments}] but none was provided to the call.")
            }

        } else {
            Boolean allValuesPassed = (results.count { k, v -> v != null } == results.size())
            if (allValuesPassed) {
                return results
            } else {
                String prettyArguments = requiredArgNames.join(', ')
                String prettyMissingArguments = (results.find { k, v -> v == null }).collect { it['key'] }.join(', ')
                throw new ArgsException("The method requires all of the following parameters [${prettyArguments}] but the call is missing [${prettyMissingArguments}].")
            }
        }
    }

    static private Object getRequiredArgument(Map args, String requiredArgName) {
        if (args == null) args = [:]

        Object requiredArg = args[requiredArgName]
        if (requiredArg == null || requiredArg == '') {
            return null

// TODO: Gianluca, non ricordo perché esiste questa logica.. ??
//        } else if (Element.isDomainClass(requiredArg.getClass())) {
//            return requiredArg['id']

        } else {
            return requiredArg
        }
    }

}