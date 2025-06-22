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

import com.jcraft.jsch.*
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
class SSHUtils {

    static Boolean verifyConnection(String host, Integer port, String username, String password, String indent = '') {
        println "${indent}Verifing connection to 'sftp://${host}:${port}'"
        try {
            connect(host, port, username, password, indent)
            return true

        } catch (Exception e) {
            e.printStackTrace()
            return false
        }
    }

    static List<File> listFiles(String remotePath, String host, Integer port, String username, String password, String indent = '') {
        Session session = connect(host, port, username, password, indent)
        ChannelSftp channel = openChannelSFTP(session)

        List<File> files = []
        String remotePathN = FileUtils.normalizePathname(remotePath)
        Vector<ChannelSftp.LsEntry> remoteFileList = channel.ls(remotePathN) as Vector<ChannelSftp.LsEntry>
        for (remoteFile in remoteFileList) {
            ChannelSftp.LsEntry file = remoteFile
            if (!file.attrs.isDir()) {
                File remotePathFile = new File(remotePathN)
                String absoluteFilename = "${remotePathFile.parent}/${file.filename}"
                files.add(new File(absoluteFilename))
            }
        }

        disconnectChannel(channel)
        disconnect(session)

        return files
    }

    static void downloadFile(String remoteFile, String localFile, String host, Integer port,
                             String username, String password, String indent = '') {
        String fileFromN = FileUtils.normalizePathname(remoteFile)
        String fileToN = FileUtils.normalizePathname(localFile)
        log.info "${indent}Downloading '${fileFromN}' to '${fileToN}'"

        Session session = connect(host, port, username, password, indent)
        ChannelSftp channel = openChannelSFTP(session)

        channel.get(fileFromN, fileToN)

        disconnectChannel(channel)
        disconnect(session)
    }

    static void downloadFileToDir(String remoteFile, String localDir, String host, Integer port,
                                  String username, String password, String indent = '') {
        String filenameFrom = new File(remoteFile).name
        String localFile = FileUtils.normalizePath(localDir) + filenameFrom

        downloadFile(remoteFile, localFile, host, port, username, password, indent)
    }

    static void renameFile(String remoteFile, String remoteFileRenamed, String host, Integer port,
                           String username, String password, String indent = '') {
        String fileN = FileUtils.normalizePathname(remoteFile)
        String fileNewN = FileUtils.normalizePathname(remoteFileRenamed)
        log.info "${indent}Renaming '${fileN}' to '${fileNewN}'"

        Session session = connect(host, port, username, password, indent)
        ChannelSftp channel = openChannelSFTP(session)

        try {
            channel.rename(fileN, fileNewN)
        } catch (Exception ignore) {
            // no-op
        }

        disconnectChannel(channel)
        disconnect(session)
    }

    static void deleteFile(String remoteFile, String host, Integer port, String username, String password, String indent = '') {
        String fileN = FileUtils.normalizePathname(remoteFile)
        log.info "${indent}Deleting '${fileN}'"

        Session session = connect(host, port, username, password, indent)
        ChannelSftp channel = openChannelSFTP(session)

        try {
            channel.rm(fileN)
        } catch (Exception ignore) {
            // no-op
        }

        disconnectChannel(channel)
        disconnect(session)
    }

    static void deleteDir(String remoteDir, String host, Integer port, String username, String password, String indent = '') {
        String dirN = FileUtils.normalizePath(remoteDir)
        log.info "${indent}Deleting '${dirN}'"

        Session session = connect(host, port, username, password, indent)
        ChannelSftp channel = openChannelSFTP(session)

        try {
            channel.rmdir(dirN)
        } catch (Exception ignore) {
            // no-op
        }

        disconnectChannel(channel)
        disconnect(session)
    }

    static void deleteDirTree(String remoteDir, String host, Integer port, String username, String password, String indent = '') {
        String dirN = FileUtils.normalizePath(remoteDir)
        log.info "${indent}Deleting '${dirN}'"

        String command = "rm -rf ${dirN}"
        execute(command, host, port, username, password, indent)
    }

    static void uploadFileToDir(String localFile, String remoteDir, String host, Integer port,
                                String username, String password, String indent = '') {
        String localFileN = FileUtils.normalizePathname(localFile)
        String remoteDirN = FileUtils.normalizePath(remoteDir)
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

    static void uploadFile(String localFile, String remoteFile, String host, Integer port,
                           String username, String password, String indent = '') {
        String fileFromN = FileUtils.normalizePathname(localFile)
        String fileToN = FileUtils.normalizePathname(remoteFile)
        log.info "${indent}Uploading '${fileFromN}' to '${fileToN}'"

        Session session = connect(host, port, username, password, indent)
        ChannelSftp channel = openChannelSFTP(session)

        channel.put(fileFromN, fileToN)

        disconnectChannel(channel)
        disconnect(session)
    }

    static String execute(String command, String host, Integer port, String username, String password, String indent = '') {
        Session session = connect(host, port, username, password, indent)
        ChannelExec channel = openChannelEXEC(session, command)

        InputStream is = channel.getInputStream()
        BufferedReader reader = new BufferedReader(new InputStreamReader(is))

        String line = ''
        String result = ''
        while ((line = reader.readLine()) != null) {
            result += line
        }

        disconnectChannel(channel)
        disconnect(session)

        return result
    }

    private static Session connect(String host, Integer port, String username, String password, String indent = '') {
        JSch jsch = new JSch()
        Session session = jsch.getSession(username, host, port)
        session.setConfig("StrictHostKeyChecking", "no")
        session.setPassword(password)
        session.connect()
        return session
    }

    private static ChannelSftp openChannelSFTP(Session session) {
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp")
        channel.connect()
        return channel
    }

    private static ChannelExec openChannelEXEC(Session session, String command) {
        ChannelExec channel = (ChannelExec) session.openChannel("exec")
        channel.command = command
        channel.connect()
        return channel
    }

    private static void disconnectChannel(Channel channel) {
        channel.disconnect()
    }

    private static void disconnect(Session session, indent = '') {
        session.disconnect()
    }

}
