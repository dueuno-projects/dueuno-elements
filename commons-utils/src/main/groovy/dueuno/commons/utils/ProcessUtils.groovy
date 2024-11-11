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

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
class ProcessUtils {

    static String execute(String command) {
        StringBuilder sout = new StringBuilder(), serr = new StringBuilder()
        Integer exitCode = execute(command, sout, serr)
        if (exitCode != 0) {
            throw new Exception("Program terminated with exit code ${exitCode}")
        }
        return "${sout} ${serr}"
    }

    static Integer execute(String command, StringBuilder sout, StringBuilder serr) {
        Process proc = command.execute()
        proc.consumeProcessOutput(sout, serr)
        proc.waitFor()

        return proc.exitValue()
    }

    static Integer execute(String command, Closure closure) {
        Process proc = command.execute()
        proc.in.eachLine { line ->
            closure.call(line)
        }
        proc.err.eachLine { line ->
            closure.call(line)
        }
        proc.waitFor()

        return proc.exitValue()
    }

}
