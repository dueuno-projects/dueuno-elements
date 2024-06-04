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
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply
import org.apache.commons.net.ftp.FTPSClient

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.Paths

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
class FTPSUtils {

    static Boolean verifyConnection(String host, Integer port, String username, String password, String indent = '') {
        log.info "${indent}Verifing connection to 'ftps://${host}:${port}'"
        try {
            connect(host, port, username, password, indent)
            return true

        } catch (Exception e) {
            return false
        }
    }

    static List<File> listFiles(String remotePath, String host, Integer port, String username, String password, String indent = '') {
        String remotePathN = FileUtils.normalizePathname(remotePath)
        File remotePathFile = new File(remotePathN)
        String remoteDirN = FileUtils.normalizeDirname(remotePathFile.parent)
        String filenameGlob = "glob:${remotePathFile.name}"

        FTPSClient ftp = connect(host, port, username, password, indent)

        ftp.execPROT("P") // encrypt data channel
        verifyErrors(ftp, indent * 2)

        FTPFile[] remoteFileList = ftp.listFiles(remoteDirN)
        verifyErrors(ftp, indent * 2)

        List<File> files = []
        for (remoteFile in remoteFileList) {
            FTPFile file = remoteFile
            String absoluteFilename = "${remoteDirN}${file.name}"

            PathMatcher matcher = FileSystems.getDefault().getPathMatcher(filenameGlob)
            if (matcher.matches(Paths.get(file.name))) {
                files.add(new File(absoluteFilename))
            }
        }

        disconnect(ftp, indent)
        return files
    }

    static void downloadFile(String remoteFile, String localFile, String host, Integer port, String username, String password, String indent = '') {
        String remoteFileN = FileUtils.normalizePathname(remoteFile)
        String localFileN = FileUtils.normalizePathname(localFile)
        log.info "${indent}Downloading from 'ftps://${host}:${port}${remoteFileN}' to '${localFileN}'"
        FTPSClient ftp = connect(host, port, username, password, indent)

        ftp.bufferSize = 0
        ftp.enterLocalPassiveMode()
        ftp.fileType = FTP.BINARY_FILE_TYPE
        verifyErrors(ftp, indent * 2)

        ftp.execPROT("P") // encrypt data channel
        verifyErrors(ftp, indent * 2)

        OutputStream fos = new BufferedOutputStream(new FileOutputStream(localFileN))
        ftp.retrieveFile(remoteFileN, fos)
        fos.close()
        verifyErrors(ftp, indent * 2)

        disconnect(ftp, indent)
    }

    static void downloadFileToDir(String remoteFile, String localDir, String host, Integer port,
                                  String username, String password, String indent = '') {
        String filenameFrom = new File(remoteFile).name
        String localFile = FileUtils.normalizeDirname(localDir) + filenameFrom

        downloadFile(remoteFile, localFile, host, port, username, password, indent)
    }

    static void renameFile(String remoteFile, String remoteFileRenamed, String host, Integer port,
                           String username, String password, String indent = '') {
        String fileN = FileUtils.normalizePathname(remoteFile)
        String fileNewN = FileUtils.normalizePathname(remoteFileRenamed)
        log.info "${indent}Renaming '${fileN}' in '${fileNewN}'"

        FTPSClient ftp = connect(host, port, username, password, indent)

        try {
            ftp.rename(fileN, fileNewN)
            verifyErrors(ftp, indent * 2)

        } catch (e) {
            log.warn e.message
        }

        disconnect(ftp, indent)
    }

    static void deleteFile(String remoteFile, String host, Integer port, String username, String password, String indent = '') {
        String fileN = FileUtils.normalizePathname(remoteFile)
        log.info "${indent}Deleting '${fileN}'"

        FTPSClient ftp = connect(host, port, username, password, indent)

        try {
            ftp.deleteFile(fileN)
            verifyErrors(ftp, indent * 2)

        } catch (e) {
            log.warn e.message
        }

        disconnect(ftp, indent)
    }

    static void deleteDir(String remoteDir, String host, Integer port, String username, String password, String indent = '') {
        String dirN = FileUtils.normalizeDirname(remoteDir)
        log.info "${indent}Deleting '${dirN}'"

        FTPSClient ftp = connect(host, port, username, password, indent)

        try {
            ftp.removeDirectory(dirN)
            verifyErrors(ftp, indent * 2)

        } catch (e) {
            log.warn e.message
        }

        disconnect(ftp, indent)
    }

    static void createFolder(String folder, String host, Integer port, String username, String password, String indent = '') {
        log.info "${indent}Creating 'ftps://${host}:${port}${folder}'"
        FTPSClient ftp = connect(host, port, username, password, indent)

        try {
            ftp.makeDirectory(folder)
            verifyErrors(ftp, indent * 2)

        } catch (e) {
            log.warn e.message
        }

        disconnect(ftp, indent)
    }

    static void uploadFileToDir(String localFile, String remoteDir, String host, Integer port, String username, String password, String indent = '') {
        String localFileN = FileUtils.normalizePathname(localFile)
        String remoteDirN = FileUtils.normalizeDirname(remoteDir)
        Path localFilePath = Paths.get(localFileN)
        String remoteFileN = "${remoteDirN}${localFilePath.fileName}"

        uploadFile(
                localFileN,
                remoteFileN,
                host,
                port,
                username,
                password,
                indent
        )
    }

    static void uploadFile(String localFile, String remoteFile, String host, Integer port, String username, String password, String indent = '') {
        log.info "${indent}Uploading '${localFile}' to 'ftps://${host}:${port}${remoteFile}'"
        FTPSClient ftp = connect(host, port, username, password, indent)

        ftp.bufferSize = 0
        ftp.fileType = FTP.BINARY_FILE_TYPE
        ftp.enterLocalPassiveMode()
        verifyErrors(ftp, indent * 2)

        ftp.execPROT("P") // encrypt data channel
        verifyErrors(ftp, indent * 2)

        def fis = new FileInputStream(localFile)
        ftp.storeFile(remoteFile, fis)
        fis.close()
        verifyErrors(ftp, indent * 2)

        disconnect(ftp, indent)
    }

    private static FTPSClient connect(String host, Integer port, String username, String password, String indent = '') {
        FTPSClient ftp = new FTPSClient()

        ftp.connect(host, port)
        verifyErrors(ftp, indent * 2)

        ftp.login(username, password)
        verifyErrors(ftp, indent * 2)

        return ftp
    }

    private static void disconnect(FTPSClient ftp, indent = '') {
        ftp.logout()
        ftp.disconnect()
    }

    private static void verifyErrors(FTPSClient ftp, String indent = '') {
        //log.info "${indent}${ftp.replyString}" - "\r\n"

        if (!FTPReply.isPositiveCompletion(ftp.replyCode)) {
            throw new Exception("FTPS ERROR: ${ftp.replyString}")
        }
    }

}
