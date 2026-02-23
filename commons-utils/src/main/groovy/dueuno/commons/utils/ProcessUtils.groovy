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
 * Utility class for executing system commands and capturing their output.
 * <p>
 * This class provides methods to execute external commands, retrieve their
 * standard and error output, and handle exit codes.
 * </p>
 *
 * <h3>Example usage:</h3>
 * <pre>
 * // Execute a command and get combined output
 * String result = ProcessUtils.execute("echo Hello")
 *
 * // Execute a command and capture output in StringBuilder objects
 * StringBuilder out = new StringBuilder(), err = new StringBuilder()
 * int exitCode = ProcessUtils.execute("ls /", out, err)
 *
 * // Execute a command and process output line by line
 * ProcessUtils.execute("ls /") { line ->
 *     println "Output line: $line"
 * }
 * </pre>
 *
 * Author: Gianluca Sartori
 */
@Slf4j
@CompileStatic
class ProcessUtils {

    /**
     * Executes a system command and returns the combined standard and error output as a string.
     * Throws an exception if the process terminates with a non-zero exit code.
     *
     * @param command the system command to execute
     * @return combined standard output and error output of the command
     * @throws Exception if the process exit code is non-zero
     *
     * <h3>Example:</h3>
     * <pre>
     * String result = ProcessUtils.execute("echo Hello")
     * </pre>
     */
    static String execute(String command) {
        StringBuilder sout = new StringBuilder(), serr = new StringBuilder()
        Integer exitCode = execute(command, sout, serr)
        if (exitCode != 0) {
            throw new Exception("Program terminated with exit code ${exitCode}")
        }
        return "${sout} ${serr}"
    }

    /**
     * Executes a system command and captures its standard output and error output in the provided StringBuilder objects.
     *
     * @param command the system command to execute
     * @param sout StringBuilder to capture standard output
     * @param serr StringBuilder to capture error output
     * @return the exit code of the process
     *
     * <h3>Example:</h3>
     * <pre>
     * StringBuilder out = new StringBuilder(), err = new StringBuilder()
     * int exitCode = ProcessUtils.execute("ls /", out, err)
     * </pre>
     */
    static Integer execute(String command, StringBuilder sout, StringBuilder serr) {
        Process proc = command.execute()
        proc.consumeProcessOutput(sout, serr)
        proc.waitFor()

        return proc.exitValue()
    }

    /**
     * Executes a system command and processes each line of standard and error output using the given closure.
     *
     * @param command the system command to execute
     * @param closure a closure that will be called for each line of output (both stdout and stderr)
     * @return the exit code of the process
     *
     * <h3>Example:</h3>
     * <pre>
     * ProcessUtils.execute("ls /") { line ->
     *     println "Output line: $line"
     * }
     * </pre>
     */
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
