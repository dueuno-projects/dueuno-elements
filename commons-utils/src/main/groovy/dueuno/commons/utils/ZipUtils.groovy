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

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Utility class for handling ZIP compression and decompression.
 * <p>
 * Provides methods to zip an entire directory into a ZIP file and
 * to extract the contents of a ZIP file into a target directory.
 * </p>
 *
 * Author: Gianluca Sartori
 */
@Slf4j
@CompileStatic
class ZipUtils {

    /**
     * Zips the contents of a directory into a ZIP file.
     * <p>
     * Preserves the directory structure and file modification times.
     * </p>
     *
     * @param fromDir the source directory to zip
     * @param toZipFile the target ZIP file path
     * @param indent optional string prefix used for logging indentation
     */
    static void zipDir(String fromDir, String toZipFile, String indent = '') {
        String fromDirN = FileUtils.normalizePath(fromDir)
        String toZipFileN = FileUtils.normalizePathname(toZipFile)
        File sourceDir = new File(fromDirN)
        File destZipFile = new File(toZipFileN)
        ZipOutputStream zis = new ZipOutputStream(new FileOutputStream(toZipFileN))

        log.info "${indent}Zipping '${sourceDir}' to '${destZipFile}'"
        sourceDir.eachFileRecurse { file ->
            String absoluteFilename = FileUtils.normalizePathname(file.toString())
            String rootPath = fromDirN
            String relativeFilename = absoluteFilename - rootPath

            if (file.isDirectory()) {
                relativeFilename = FileUtils.normalizePath(relativeFilename)
            }

            log.info "${indent}${indent}Adding '${Paths.get(relativeFilename)}'"
            ZipEntry entry = new ZipEntry(relativeFilename)
            entry.time = file.lastModified()
            zis.putNextEntry(entry)

            if (file.isFile()) {
                zis << new FileInputStream(file)
            }
        }
        zis.close()
    }

    /**
     * Extracts the contents of a ZIP file into a target directory.
     * <p>
     * Preserves the directory structure of the ZIP file and overwrites
     * existing files if necessary.
     * </p>
     *
     * @param fromZipFile the path of the ZIP file to extract
     * @param toDir the target directory to extract the files into
     * @param indent optional string prefix used for logging indentation
     */
    static void unzipFile(String fromZipFile, String toDir, String indent = '') {
        String fromZipFileN = FileUtils.normalizePathname(fromZipFile)
        String toDirN = FileUtils.normalizePath(toDir)
        ZipFile zipFile = new ZipFile(new File(fromZipFileN))

        log.info "${indent}Unzipping '${Paths.get(fromZipFileN)}' to '${Paths.get(toDirN)}'"
        Enumeration<ZipEntry> entries = zipFile.entries() as Enumeration<ZipEntry>
        for (entry in entries) {
            Path outputFile = Paths.get(toDirN + entry.name)

            log.info "${indent}${indent}Unzipping '${outputFile}'"
            if (entry.isDirectory()) {
                Files.createDirectories(outputFile)

            } else {
                def parentDir = outputFile.getParent()
                if (!Files.exists(parentDir)) {
                    Files.createDirectories(parentDir)
                }

                Files.copy(zipFile.getInputStream(entry), outputFile, StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

}
