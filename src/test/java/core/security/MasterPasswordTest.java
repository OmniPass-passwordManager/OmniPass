package core.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MasterPasswordTest {

    private static final Path MASTER_FILE = Path.of("data/master.dat");

    private byte[] originalMasterFile;
    private boolean masterFileExisted;

    @BeforeEach
    void backupMasterFile() throws Exception {
        masterFileExisted = Files.exists(MASTER_FILE);

        if (masterFileExisted) {
            originalMasterFile = Files.readAllBytes(MASTER_FILE);
        }
    }

    @AfterEach
    void restoreMasterFile() throws Exception {
        if (masterFileExisted) {
            Files.createDirectories(MASTER_FILE.getParent());
            Files.write(MASTER_FILE, originalMasterFile);
        } else {
            Files.deleteIfExists(MASTER_FILE);
        }
    }

    @Test
    void generateCredentialsCreatesSalt() {
        MasterPassword.Credentials credentials =
                MasterPassword.generateCredentials("test-password");

        assertNotNull(credentials.salt());
        assertEquals(16, credentials.salt().length);
    }

    @Test
    void generateCredentialsCreatesHash() {
        MasterPassword.Credentials credentials =
                MasterPassword.generateCredentials("test-password");

        assertNotNull(credentials.hash());
        assertFalse(credentials.hash().isEmpty());
    }

    @Test
    void generateCredentialsCreatesKey() {
        MasterPassword.Credentials credentials =
                MasterPassword.generateCredentials("test-password");

        assertNotNull(credentials.key());
        assertEquals("AES", credentials.key().getAlgorithm());
        assertEquals(32, credentials.key().getEncoded().length);
    }

    @Test
    void samePasswordDifferentCredentials() {
        MasterPassword.Credentials credentials1 =
                MasterPassword.generateCredentials("test-password");

        MasterPassword.Credentials credentials2 =
                MasterPassword.generateCredentials("test-password");

        assertFalse(
                java.util.Arrays.equals(
                        credentials1.salt(),
                        credentials2.salt()
                )
        );
    }

    @Test
    void correctPasswordAuthenticates() {
        MasterPassword.create("test-password");

        SecretKey key = MasterPassword.authenticate("test-password");

        assertNotNull(key);
        assertEquals("AES", key.getAlgorithm());
    }

    @Test
    void wrongPasswordFails() {
        MasterPassword.create("test-password");

        SecretKey key = MasterPassword.authenticate("wrong-password");

        assertNull(key);
    }

    @Test
    void malformedMasterFileFails() throws Exception {
        Files.createDirectories(MASTER_FILE.getParent());

        Files.writeString(
                MASTER_FILE,
                "invalid-master-password-file",
                StandardCharsets.UTF_8
        );

        SecretKey key = MasterPassword.authenticate("test-password");

        assertNull(key);
    }

    @Test
    void missingMasterFileFails() throws Exception {
        Files.deleteIfExists(MASTER_FILE);

        SecretKey key = MasterPassword.authenticate("test-password");

        assertNull(key);
    }
}