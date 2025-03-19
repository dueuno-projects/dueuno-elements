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

    static String normalizePathname(String pathname) {
        if (!pathname) {
            return pathname
        }

        return pathname.replace("\\", "/")
    }

    static String normalizePath(String path) {
        if (!path) {
            return path
        }

        String result = normalizePathname(path)
        if (!result.endsWith('/')) {
            result += '/'
        }

        return result
    }

    static String stripPath(String pathname) {
        if (!pathname) {
            return pathname
        }

        def f = normalizePathname(pathname)
        int index = f.lastIndexOf('/')
        if (index == -1) {
            return ''
        }

        String result = f.substring(0, index + 1)
        return result
    }

    static String stripFilename(String pathname) {
        if (!pathname) {
            return pathname
        }

        def f = normalizePathname(pathname)
        int index = f.lastIndexOf('/')
        String result = f.substring(index + 1)
        return result
    }

    static String stripExtension(String pathname) {
        if (!pathname) {
            return pathname
        }

        def dot = pathname.lastIndexOf('.')
        return pathname
                .substring(dot + 1)
                .toLowerCase()
    }

    static String removeExtension(String pathname) {
        if (!pathname) {
            return pathname
        }

        def f = normalizePathname(pathname)
        int index = f.lastIndexOf('.')
        String result = f.substring(0, index)
        return result
    }

    static String buildSafeFilename(String filename, String spaceReplacementChar = '-') {
        def unixFilenameInvalidChars = '[/]'
        def unixFilenameInvalidNonPrintableChars = '[\\u0000]' // NULL
        def win32FilenameInvalidChars = '[<>:"/\\\\|?*]'
        def win32FilenameInvalidNonPrintableChars = '[\\u0000-\\u001F]|[\\u007F]' // ASCII 0-31 e 127
        def urlInvalidChars = '[<>#%\\+\\{\\}|\\^~\\[\\]`;/\\?:@=&$]'
        def space = '[ ]'

        return filename.replaceAll(
                unixFilenameInvalidChars
                        + '|' + unixFilenameInvalidNonPrintableChars
                        + '|' + win32FilenameInvalidChars
                        + '|' + win32FilenameInvalidNonPrintableChars
                        + '|' + urlInvalidChars
                        + '|' + space,
                spaceReplacementChar)
    }

    static String getTempDirectory() {
        def result = System.getProperty("java.io.tmpdir")
        return normalizePath(result)
    }

    static String getWorkingDirectory() {
        Path userDirPath = Paths.get(System.getProperty("user.dir")).normalize()
        return normalizePath(userDirPath.toString())
    }

    static String getTempFilename(Integer length = 10) {
        String token = StringUtils.generateRandomToken(length)
        def fileName = "${getFilenameTimestamp('yyyyMMddHHmmssS')}-${token}"
        return fileName
    }

    static String getApplicationPath() {
        Path workDirPath = Paths.get(FileUtils.getProtectionDomain().getCodeSource().getLocation().toString()).normalize()
        return normalizePath(workDirPath.toString())
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

    static List<File> listFiles(String path) {
        return listFiles(path, '*')
    }

    static List<File> listFiles(String path, String pattern) {
        Path folderPath = Paths.get(path)
        DirectoryStream<Path> dir = Files.newDirectoryStream(folderPath, pattern)

        List<File> results = []
        for (part in dir) {
            if (!Files.isDirectory(part)) {
                results.add(part.toFile())
            }
        }
        return results
    }

    static void createFile(String pathname, String indent = '') {
        String newPathname = normalizePathname(pathname)

        log.info "${indent}Creating file '${newPathname}'"
        File emptyFile = new File(newPathname)
        emptyFile.createNewFile()
    }

    static void deleteFile(String pathname, String indent = '') {
        String deletePathname = normalizePathname(pathname)
        Path fileToDelete = Paths.get(deletePathname)

        log.info "${indent}Deleting '${fileToDelete}'"
        Files.deleteIfExists(fileToDelete)
    }

    static void renameFile(String fromPathname, String toPathname, String indent = '') {
        String renameFromPathname = normalizePathname(fromPathname)
        String renameToPathname = normalizePathname(toPathname)
        File from = new File(renameFromPathname)
        File to = new File(renameToPathname)

        log.info "${indent}Renaming '${from}' to '${to}'"
        from.renameTo(to)
    }

    static void copyFile(String fromPathname, String toPathname, String indent = '') {
        String copyFromPathname = normalizePathname(fromPathname)
        String copyToPathname = normalizePathname(toPathname)
        Path from = Paths.get(copyFromPathname)
        Path to = Paths.get(copyToPathname)

        log.info "${indent}Copying '${from}' to '${to}'"
        Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING)
    }

    static void copyFileToDirectory(String fromPathname, String toPath, String indent = '') {
        String copyFromPathname = normalizePathname(fromPathname)
        String copyToPath = normalizePath(toPath)
        Path from = Paths.get(copyFromPathname)
        Path to = Paths.get(copyToPath)
        Path toFile = Paths.get("${to}/${from.fileName}")

        log.info "${indent}Copying '${from}' to '${toFile}'"
        Files.copy(from, toFile, StandardCopyOption.REPLACE_EXISTING)
    }

    static String moveFile(String fromPathname, String toPathname, String indent = '') {
        String moveFromPathname = normalizePathname(fromPathname)
        String moveToPathname = normalizePathname(toPathname)
        Path from = Paths.get(moveFromPathname)
        Path to = Paths.get(moveToPathname)

        log.info "${indent}Moving '${from}' to '${to}'"
        return Files.move(from, to, StandardCopyOption.REPLACE_EXISTING).toString()
    }

    static String moveFileToDirectory(String fromPathname, String toPath, String indent = '') {
        String moveFromPathname = normalizePathname(fromPathname)
        String moveToPath = normalizePath(toPath)
        Path from = Paths.get(moveFromPathname)
        Path toDir = Paths.get(moveToPath)
        Path to = Paths.get("${toDir}/${from.fileName}")

        log.info "${indent}Moving '${from}' to '${to}'"
        return Files.move(from, to, StandardCopyOption.REPLACE_EXISTING).toString()
    }

    static void createDirectory(String path, String indent = '') {
        String newPath = normalizePath(path)
        Path pathToCreate = Paths.get(newPath)

        if (Files.notExists(pathToCreate)) {
            log.info "${indent}Creating '${pathToCreate}'"
            Files.createDirectories(pathToCreate)
        }
    }

    static void deleteDirectory(String path, String indent = '') {
        String deleteDir = normalizePath(path)
        Path dirToDelete = Paths.get(deleteDir)
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

    static void copyDirectory(String fromPath, String toPath, String indent = '') {
        String copyFromPath = normalizePath(fromPath)
        String copyToPath = normalizePath(toPath)

        log.info "${indent}Copying '${Paths.get(copyFromPath)}' to '${Paths.get(copyToPath)}'"
        copyDirectoryRecursive(copyFromPath, copyToPath, indent)
        log.info "${indent}... done."
    }

    private static void copyDirectoryRecursive(String fromPath, String toPath, String indent = '') {
        createDirectory(toPath)
        DirectoryStream<Path> directory = Files.newDirectoryStream(Paths.get(fromPath))
        for (path in directory) {
            String copyFromPath = path
            String copyToPath = "${toPath}/${path.getFileName()}"
            if (Files.isDirectory(path)) {
                copyDirectoryRecursive(copyFromPath, copyToPath, indent)
            } else {
                copyFile(copyFromPath, copyToPath, indent)
            }
        }
    }

}
