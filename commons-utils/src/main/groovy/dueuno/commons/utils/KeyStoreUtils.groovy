package dueuno.commons.utils

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.crypto.SecretKey
import javax.crypto.spec.PBEParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.PosixFilePermission
import java.security.KeyStore
import java.security.SecureRandom

@Slf4j
@CompileStatic
class KeyStoreUtils {

    private static final String KEYSTORE_TYPE = "PKCS12"
    private static final String PROTECTION_ALGORITHM = "PBEWithHmacSHA256AndAES_256"
    private static final byte[] FIXED_SALT = "keystore-utils-salt".bytes
    private static final int ITERATIONS = 300_000

    static KeyStore create(byte[] password) {
        return load(null, password)
    }

    static byte[] generateKeyStorePassword() {
        // Generate 256-bit random seed (32 bytes)
        byte[] keyStorePassword = new byte[32]
        new SecureRandom().nextBytes(keyStorePassword)
        return keyStorePassword
    }

    static void saveKeyStorePassword(byte[] password, String pathname) {
        Path path = Paths.get(pathname)
        Files.write(path, password)

        // Set file permissions if the filesystem supports POSIX
        if (!Files.getFileStore(path).supportsFileAttributeView("posix")) {
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

    static byte[] loadKeyStorePassword(String pathname) {
        Path path = Paths.get(pathname)

        try {
            byte[] keyStorePassword = Files.readAllBytes(path)
            return keyStorePassword

        } catch (Exception e) {
            log.error "Error loading '${pathname}'"
            log.info LogUtils.logStackTrace(e)
            return null
        }
    }

    static KeyStore loadFromFile(String filename, byte[] password) {
        try (FileInputStream is = new FileInputStream(filename)) {
            return load(is, password)

        } catch (Exception e) {
            log.warn "Cannot access key chain at '${filename}': ${e.message}"
            return load(null, password)
        }
    }

    static void saveToFile(KeyStore keyStore, byte[] password, String filename) {
        try (FileOutputStream os = new FileOutputStream(filename)) {
            save(keyStore, password, os)
        }
    }

    static KeyStore loadFromString(String base64String, byte[] password) {
        byte[] data = Base64.decoder.decode(base64String)
        try (InputStream is = new ByteArrayInputStream(data)) {
            return load(is, password)
        }
    }

    static String saveToString(KeyStore keyStore, byte[] password) {
        ByteArrayOutputStream os = new ByteArrayOutputStream()
        save(keyStore, password, os)
        return Base64.encoder.encodeToString(os.toByteArray())
    }

    static void setKey(KeyStore keyStore, byte[] password, String name, String value) {
        byte[] keyBytes = value.getBytes(StandardCharsets.UTF_8)
        SecretKey keySpec = new SecretKeySpec(keyBytes, "AES")
        KeyStore.SecretKeyEntry secretKey = new KeyStore.SecretKeyEntry(keySpec)

        keyStore.setEntry(name, secretKey, buildProtection(password))
    }

    static String getKey(KeyStore keyStore, byte[] password, String name) {
        KeyStore.SecretKeyEntry entry =
                keyStore.getEntry(name, buildProtection(password)) as KeyStore.SecretKeyEntry
        return entry
                ? new String(entry.secretKey.encoded, StandardCharsets.UTF_8)
                : null
    }

    static boolean contains(KeyStore keyStore, String alias) {
        return keyStore.containsAlias(alias)
    }


    private static KeyStore load(InputStream is, byte[] password) {
        char[] pwd = passwordBytesToChars(password)

        try {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE)
            keyStore.load(is, pwd)
            return keyStore

        } catch (Exception e) {
            throw new IOException("Error loading KeyStore: ${e.message}", e)
        }
    }

    private static void save(KeyStore keyStore, byte[] password, OutputStream os) {
        char[] pwd = passwordBytesToChars(password)
        try {
            keyStore.store(os, pwd)

        } catch (Exception e) {
            throw new IOException("Error saving KeyStore: ${e.message}", e)
        }
    }

    private static KeyStore.PasswordProtection buildProtection(byte[] password) {
        char[] pwd = passwordBytesToChars(password)
        PBEParameterSpec spec = new PBEParameterSpec(FIXED_SALT, ITERATIONS)
        return new KeyStore.PasswordProtection(pwd, PROTECTION_ALGORITHM, spec)
    }

    private static char[] passwordBytesToChars(byte[] passwordBytes) {
        return Base64.encoder.encodeToString(passwordBytes).toCharArray()
    }
}
