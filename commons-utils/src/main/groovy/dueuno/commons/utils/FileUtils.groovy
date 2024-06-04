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

import java.nio.file.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
class FileUtils {

    static String normalizePathname(String path) {
        if (!path) {
            return ''
        }

        return path.replace("\\", "/")
    }

    static String normalizeDirname(String path) {
        if (!path) {
            return ''
        }

        String result = normalizePathname(path)
        if (!result.endsWith('/')) {
            result += '/'
        }

        return result
    }

    static String stripDirname(String dirname) {
        def f = normalizePathname(dirname)
        int index = f.lastIndexOf('/')
        if (index == -1) {
            return ''
        }

        String result = f.substring(0, index + 1)
        return result
    }

    static String stripFilename(String pathname) {
        def f = normalizePathname(pathname)
        int index = f.lastIndexOf('/')
        String result = f.substring(index + 1)
        return result
    }

    static String stripExtension(String filename) {
        if (!filename) {
            return ''
        }

        def dot = filename.lastIndexOf('.')
        return filename
                .substring(dot + 1)
                .toLowerCase()
    }

    static String removeExtension(String filename) {
        def f = normalizePathname(filename)
        int index = f.lastIndexOf('.')
        String result = f.substring(0, index)
        return result
    }

    static String buildSafeFilename(String filename) {
        def unixFilenameInvalidChars = '[/]'
        def unixFilenameInvalidNonPrintableChars = '[\\u0000]' // NULL
        def win32FilenameInvalidChars = '[<>:"/\\\\|?*]'
        def win32FilenameInvalidNonPrintableChars = '[\\u0000-\\u001F]|[\\u007F]' // ASCII 0-31 e 127
        def space = '[ ]'

        return filename.replaceAll(
                unixFilenameInvalidChars + '|' +
                        unixFilenameInvalidNonPrintableChars + '|' +
                        win32FilenameInvalidChars + '|' +
                        win32FilenameInvalidNonPrintableChars + '|' +
                        space, '_')
    }

    static String getTempDir() {
        def result = System.getProperty("java.io.tmpdir")
        return normalizeDirname(result)
    }

    static String getWorkDir() {
        Path workDirPath = Paths.get(System.getProperty("user.dir")).normalize()
        return normalizeDirname(workDirPath.toString())
    }

    static String getTempFilename(Integer length = 10) {
        String token = StringUtils.generateRandomToken(length)
        def fileName = "${getFilenameTimestamp('yyyyMMddHHmmssS')}-${token}"
        return fileName
    }

    static String getApplicationDir() {
        Path workDirPath = Paths.get(FileUtils.getProtectionDomain().getCodeSource().getLocation().toString()).normalize()
        return normalizeDirname(workDirPath.toString())
    }

    static String getFilenameTimestamp(String pattern = 'YYYYMMddHHmm') {
        return getFilenameTimestamp(LocalDateTime.now(), pattern)
    }

    static String getFilenameTimestamp(LocalDateTime dateTime, String pattern = 'YYYYMMddHHmm') {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern)
        return dateTime.format(format)
    }

    static String serializeLogFilename(String... parts) {
        serializeLogFilename(LocalDateTime.now(), parts)
    }

    static String serializeLogFilename(LocalDateTime dateTime, Object... parts) {
        String result = getFilenameTimestamp(dateTime)
        for (part in parts) {
            result += '_' + part.toString().toLowerCase()
        }
        result += '.log'
        return result
    }

    static Map deserializeLogFilename(String logFilename, String... parts) {
        String fileName = stripFilename(logFilename)
        String fileExt = stripExtension(logFilename)
        List fileParts = fileName.split('_') as List
        Integer filePartsCount = fileParts.size() - 1
        Integer userPartsCount = parts.size()
        Map result = [:]

        if (filePartsCount < 2 || fileExt != 'log') {
            throw new Exception("Invalid log file (does not have'.log' extension and does not have at least 2 parts separated by '_')")
        }

        if (filePartsCount != userPartsCount) {
            throw new Exception("Error deserializing filename '${logFilename}'. Cannot match filename " +
                    "'${filePartsCount}' parts with the supplied '${userPartsCount}' parts: ${parts}.")
        }

        result['timestamp'] = fileParts[0]
        Integer i = 1
        for (part in parts) {
            result[part] = fileParts[i]
            i++
        }

        return result
    }

    static void touch(String pathname) {
        File f = new File(pathname)
        if (!f.exists()) {
            new FileOutputStream(f).close()
        }
        f.setLastModified(System.currentTimeMillis())
    }

    static Boolean exists(String pathname) {
        File f = new File(pathname)
        return f.exists()
    }

    static List<File> listFiles(String directory) {
        return listFiles(directory, '*')
    }

    static List<File> listFiles(String folder, String pattern) {
        Path folderPath = Paths.get(folder)
        DirectoryStream<Path> dir = Files.newDirectoryStream(folderPath, pattern)

        List<File> results = []
        for (path in dir) {
            if (!Files.isDirectory(path)) {
                results.add(path.toFile())
            }
        }
        return results
    }

    static void createFile(String file, String indent = '') {
        String fileN = normalizePathname(file)

        log.info "${indent}Creating file '${fileN}'"
        File emptyFile = new File(file)
        emptyFile.createNewFile()
    }

    static void deleteFile(String file, String indent = '') {
        String fileN = normalizePathname(file)
        Path fileToDelete = Paths.get(fileN)

        log.info "${indent}Deleting '${fileToDelete}'"
        Files.deleteIfExists(fileToDelete)
    }

    static void renameFile(String fromFile, String toFile, String indent = '') {
        String fromFileN = normalizePathname(fromFile)
        String toFileN = normalizePathname(toFile)
        File from = new File(fromFileN)
        File to = new File(toFileN)

        log.info "${indent}Renaming '${from}' to '${to}'"
        from.renameTo(to)
    }

    static void copyFile(String fromFile, String toFile, String indent = '') {
        String fromFileN = normalizePathname(fromFile)
        String toFileN = normalizePathname(toFile)
        Path from = Paths.get(fromFileN)
        Path to = Paths.get(toFileN)

        log.info "${indent}Copying '${from}' to '${to}'"
        Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING)
    }

    static void copyFileToDir(String fromFile, String toDir, String indent = '') {
        String fromFileN = normalizePathname(fromFile)
        String toDirN = normalizeDirname(toDir)
        Path from = Paths.get(fromFileN)
        Path to = Paths.get(toDirN)
        Path toFile = Paths.get("${to}/${from.fileName}")

        log.info "${indent}Copying '${from}' to '${toFile}'"
        Files.copy(from, toFile, StandardCopyOption.REPLACE_EXISTING)
    }

    static String moveFile(String fromFile, String toFile, String indent = '') {
        String fromFileN = normalizePathname(fromFile)
        String toFileN = normalizePathname(toFile)
        Path from = Paths.get(fromFileN)
        Path to = Paths.get(toFileN)

        log.info "${indent}Moving '${from}' to '${to}'"
        return Files.move(from, to, StandardCopyOption.REPLACE_EXISTING).toString()
    }

    static String moveFileToDir(String fromFile, String toDir, String indent = '') {
        String fromFileN = normalizePathname(fromFile)
        String toDirN = normalizeDirname(toDir)
        Path from = Paths.get(fromFileN)
        Path to = Paths.get(toDirN)
        Path toFile = Paths.get("${to}/${from.fileName}")

        log.info "${indent}Moving '${from}' to '${toFile}'"
        return Files.move(from, toFile, StandardCopyOption.REPLACE_EXISTING).toString()
    }

    static void createDir(String dir, String indent = '') {
        String dirN = normalizeDirname(dir)
        Path dirToCreate = Paths.get(dirN)

        if (Files.notExists(dirToCreate)) {
            log.info "${indent}Creating '${dirToCreate}'"
            Files.createDirectories(dirToCreate)
        }
    }

    static void deleteDir(String dir, String indent = '') {
        String dirN = normalizeDirname(dir)
        Path dirToDelete = Paths.get(dirN)
        if (Files.notExists(dirToDelete)) {
            log.info "${indent}Deleting '${dirToDelete}' but does not exist, skipping."
            return
        }
        log.info "${indent}Deleting '${dirToDelete.fileName}'"
        Files.walk(dirToDelete)
                .sorted(Comparator.reverseOrder())
                .forEach {
                    deleteFile(it.toString(), indent * 2)
                }
        log.info "${indent}... done."
    }

    static void copyDir(String fromDir, String toDir, String indent = '') {
        String fromDirN = normalizeDirname(fromDir)
        String toDirN = normalizeDirname(toDir)

        log.info "${indent}Copying '${Paths.get(fromDirN)}' to '${Paths.get(toDirN)}'"
        copyDirRecursive(fromDirN, toDirN, indent)
        log.info "${indent}... done."
    }

    private static void copyDirRecursive(String fromDir, String toDir, String indent = '') {
        createDir(toDir)
        DirectoryStream<Path> directory = Files.newDirectoryStream(Paths.get(fromDir))
        for (path in directory) {
            String fromPath = path
            String toPath = "${toDir}/${path.getFileName()}"
            if (Files.isDirectory(path)) {
                copyDirRecursive(fromPath, toPath, indent)
            } else {
                copyFile(fromPath, toPath, indent)
            }
        }
    }

}
