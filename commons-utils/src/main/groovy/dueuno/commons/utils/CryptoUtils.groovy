package dueuno.commons.utils

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.PosixFilePermission
import java.security.SecureRandom

@Slf4j
@CompileStatic
class CryptoUtils {

    private static final String CIPHER_ALGORITHM = 'AES/CBC/PKCS5Padding'

    static byte[] generateAESKey(int bytes = 32) {
        byte[] key = new byte[bytes]
        new SecureRandom().nextBytes(key)
        return key
    }

    static void saveAESKey(byte[] password, String pathname) {
        Path path = Paths.get(pathname)
        Files.write(path, password)

        // Set file permissions if the filesystem supports POSIX
        if (!Files.getFileStore(path).supportsFileAttributeView('posix')) {
            log.warn "Non-POSIX filesystem detected. '${pathname}' file permissions were not set. Ensure the file is protected."
            return
        }

        try {
            Set<PosixFilePermission> perms = [
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE
            ] as Set
            Files.setPosixFilePermissions(path, perms)

        } catch (Exception e) {
            log.warn "Unable to set POSIX permissions: ${e.message}"
        }
    }

    static byte[] loadAESKey(String pathname) {
        try {
            Path path = Paths.get(pathname)
            byte[] keyStorePassword = Files.readAllBytes(path)
            return keyStorePassword

        } catch (Exception e) {
            log.error "Error loading '${pathname}': ${e.message}"
            log.info LogUtils.logStackTrace(e)
            return null
        }
    }

    static String encrypt(String value, byte[] symmetricKey) {
        if (!value) {
            return ''
        }

        if (!symmetricKey) {
            throw new Exception("Encryption error: no key provided.")
        }

        // Genera IV casuale
        byte[] iv = new byte[16]
        new SecureRandom().nextBytes(iv)

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        SecretKeySpec keySpec = new SecretKeySpec(symmetricKey, 'AES')
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv))

        byte[] encrypted = cipher.doFinal(value.bytes)

        // Combina IV + dati cifrati
        byte[] combined = new byte[iv.length + encrypted.length]
        System.arraycopy(iv, 0, combined, 0, iv.length)
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length)

        return Base64.encoder.encodeToString(combined)
    }

    static String decrypt(String encryptedValue, byte[] symmetricKey) {
        if (!encryptedValue) {
            return ''
        }

        byte[] data = Base64.decoder.decode(encryptedValue)
        byte[] iv = data[0..<16] as byte[]
        byte[] encrypted = data[16..-1] as byte[]

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        SecretKeySpec keySpec = new SecretKeySpec(symmetricKey, 'AES')
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv))

        byte[] decrypted = cipher.doFinal(encrypted)
        return new String(decrypted)
    }

}
