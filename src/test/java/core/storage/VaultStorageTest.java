package core.storage;

import core.PasswordEntry;
import core.Vault;
import core.crypto.EncryptionManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class VaultStorageTest {

    private final Path vaultFile = Path.of("data/test-vault.dat");

    @AfterEach 
    void cleanup() throws Exception {
        Files.deleteIfExists(vaultFile);
    }

    @Test 
    void saveAndLoadVault() {
        Vault vault = new Vault();

        PasswordEntry entry = new PasswordEntry(
            "github.com", 
            "user123", 
            "password123", 
            "My GitHub account"
        );

        vault.addEntry(entry);

        SecretKey key = EncryptionManager.deriveKey("test-password", EncryptionManager.generateSalt());

        assertTrue(VaultStorage.save(vault, vaultFile.toString(), key));

        Vault loaded = VaultStorage.load(vaultFile.toString(), key);

        assertEquals(1, loaded.getEntries().size());

        PasswordEntry loadedEntry = loaded.getEntries().get(0);

        assertEquals("github.com", loadedEntry.getWebsite());
        assertEquals("user123", loadedEntry.getUsername());
        assertEquals("password123", loadedEntry.getPassword());
        assertEquals("My GitHub account", loadedEntry.getNotes());
    }

    @Test 
    void saveAndLoadMultipleEntries() {
        Vault vault = new Vault();

        vault.addEntry(new PasswordEntry("github.com", "user1", "pass1", "notes1"));

        vault.addEntry(new PasswordEntry("google.com", "user2", "pass2", "notes2"));

        SecretKey key = EncryptionManager.deriveKey("test-password", EncryptionManager.generateSalt());

        assertTrue(VaultStorage.save(vault, vaultFile.toString(), key));

        Vault loaded = VaultStorage.load(vaultFile.toString(), key);

        assertEquals(2, loaded.getEntries().size());
    }

    @Test 
    void unicodeDataRoundTrip() {
        Vault vault = new Vault();

        vault.addEntry(new PasswordEntry(
            "例え.テスト",
            "ユーザー 🔐",
            "пароль",
            "नमस्ते 🌍"
        ));

        SecretKey key = EncryptionManager.deriveKey("test-password", EncryptionManager.generateSalt());

        assertTrue(VaultStorage.save(vault, vaultFile.toString(), key));

        Vault loaded = VaultStorage.load(vaultFile.toString(), key);

        PasswordEntry entry = loaded.getEntries().get(0);

        assertEquals("例え.テスト", entry.getWebsite());
        assertEquals("ユーザー 🔐", entry.getUsername());
        assertEquals("пароль", entry.getPassword());
        assertEquals("नमस्ते 🌍", entry.getNotes());
    }

    @Test 
    void emptyVaultRoundTrip() {
        Vault vault = new Vault();

        SecretKey key = EncryptionManager.deriveKey("test-password", EncryptionManager.generateSalt());

        assertTrue(VaultStorage.save(vault, vaultFile.toString(), key));

        Vault loaded = VaultStorage.load(vaultFile.toString(), key);

        assertTrue(loaded.getEntries().isEmpty());
    }

    @Test 
    void wrongKeyFailsToLoad() {
        Vault vault = new Vault();

        vault.addEntry(new PasswordEntry("github.com", "user123", "password123", "notes"));

        SecretKey correctkey = EncryptionManager.deriveKey("correct-password", EncryptionManager.generateSalt());

        SecretKey wrongkey = EncryptionManager.deriveKey("wrong-password", EncryptionManager.generateSalt());

        assertTrue(VaultStorage.save(vault, vaultFile.toString(), correctkey));

        assertThrows(
            RuntimeException.class,
            () -> VaultStorage.load(vaultFile.toString(), wrongkey)
        );
    }

    @Test 
    void missingVaultReturnsEmptyVault() {
        SecretKey key = EncryptionManager.deriveKey("test-password", EncryptionManager.generateSalt());

        Vault loaded = VaultStorage.load(vaultFile.toString(),key);

        assertNotNull(loaded);
        assertTrue(loaded.getEntries().isEmpty());
    }

    @Test
    void vaultFileDoesNotContainPlaintextPassword() throws Exception {
        Vault vault = new Vault();

        String password = "SuperSecretPassword123!";

        vault.addEntry(new PasswordEntry(
                "github.com",
                "user123",
                password,
                "private notes"
        ));

        SecretKey key = EncryptionManager.deriveKey(
                "test-password",
                EncryptionManager.generateSalt()
        );

        assertTrue(
                VaultStorage.save(
                        vault,
                        vaultFile.toString(),
                        key
                )
        );

        String fileContents = Files.readString(vaultFile);

        assertFalse(fileContents.contains(password));
        assertFalse(fileContents.contains("private notes"));
    }
}