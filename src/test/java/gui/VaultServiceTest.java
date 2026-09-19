package gui;


import core.PasswordEntry;
import core.Vault;

import core.security.MasterPassword;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class VaultServiceTest {

    private static final Path VAULT_FILE =
            Path.of("data/vault.dat");

    private static final Path MASTER_FILE =
            Path.of("data/master.dat");

    private static final Path EXPORT_FILE =
            Path.of("data/test-service-backup.opb");

    private byte[] originalVault;
    private byte[] originalMaster;

    private boolean vaultExisted;
    private boolean masterExisted;

    @BeforeEach
    void backupFiles() throws Exception {
        Files.createDirectories(Path.of("data"));

        vaultExisted = Files.exists(VAULT_FILE);
        masterExisted = Files.exists(MASTER_FILE);

        if (vaultExisted) {
            originalVault = Files.readAllBytes(VAULT_FILE);
        }

        if (masterExisted) {
            originalMaster = Files.readAllBytes(MASTER_FILE);
        }

        Files.deleteIfExists(VAULT_FILE);
        Files.deleteIfExists(MASTER_FILE);
        Files.deleteIfExists(EXPORT_FILE);
    }

    @AfterEach
    void restoreFiles() throws Exception {
        Files.deleteIfExists(VAULT_FILE);
        Files.deleteIfExists(MASTER_FILE);
        Files.deleteIfExists(EXPORT_FILE);

        if (vaultExisted) {
            Files.write(VAULT_FILE, originalVault);
        }

        if (masterExisted) {
            Files.write(MASTER_FILE, originalMaster);
        }
    }

    private VaultService createService(String password) {
        MasterPassword.create(password);

        SecretKey key = MasterPassword.authenticate(password);

        assertNotNull(key);

        return new VaultService(new Vault(), key);
    }

    @Test
    void addEntryPersistsEntry() {
        VaultService service = createService("test-password");

        service.addEntry(
                "github.com",
                "user123",
                "password123",
                "GitHub account"
        );

        assertEquals(1, service.getEntries().size());

        SecretKey key = MasterPassword.authenticate("test-password");

        Vault loaded = core.storage.VaultStorage.load(
                VAULT_FILE.toString(),
                key
        );

        assertEquals(1, loaded.getEntries().size());

        PasswordEntry entry = loaded.getEntries().get(0);

        assertEquals("github.com", entry.getWebsite());
        assertEquals("user123", entry.getUsername());
        assertEquals("password123", entry.getPassword());
        assertEquals("GitHub account", entry.getNotes());
    }

    @Test
    void updateEntryPersistsChanges() {
        VaultService service = createService("test-password");

        service.addEntry(
                "github.com",
                "old-user",
                "old-password",
                "old notes"
        );

        PasswordEntry entry = service.getEntries().get(0);

        service.updateEntry(
                entry,
                "google.com",
                "new-user",
                "new-password",
                "new notes"
        );

        SecretKey key = MasterPassword.authenticate("test-password");

        Vault loaded = core.storage.VaultStorage.load(
                VAULT_FILE.toString(),
                key
        );

        PasswordEntry updated = loaded.getEntries().get(0);

        assertEquals("google.com", updated.getWebsite());
        assertEquals("new-user", updated.getUsername());
        assertEquals("new-password", updated.getPassword());
        assertEquals("new notes", updated.getNotes());
    }

    @Test
    void deleteEntryPersistsRemoval() {
        VaultService service = createService("test-password");

        service.addEntry(
                "github.com",
                "user123",
                "password123",
                "notes"
        );

        PasswordEntry entry = service.getEntries().get(0);

        service.deleteEntry(entry);

        assertTrue(service.getEntries().isEmpty());

        SecretKey key = MasterPassword.authenticate("test-password");

        Vault loaded = core.storage.VaultStorage.load(
                VAULT_FILE.toString(),
                key
        );

        assertTrue(loaded.getEntries().isEmpty());
    }

    @Test
    void searchReturnsMatchingEntries() {
        VaultService service = createService("test-password");

        service.addEntry(
                "github.com",
                "user1",
                "pass1",
                "notes"
        );

        service.addEntry(
                "google.com",
                "user2",
                "pass2",
                "notes"
        );

        assertEquals(
                1,
                service.search("GITHUB").size()
        );

        assertEquals(
                "github.com",
                service.search("GITHUB")
                        .get(0)
                        .getWebsite()
        );
    }

    @Test
    void lockClearsEntries() {
        VaultService service = createService("test-password");

        service.addEntry(
                "github.com",
                "user123",
                "password123",
                "notes"
        );

        assertFalse(service.getEntries().isEmpty());

        service.lock();

        assertTrue(service.getEntries().isEmpty());
    }

    @Test
    void exportAndImportVault() {
        VaultService service = createService("test-password");

        service.addEntry(
                "github.com",
                "user123",
                "password123",
                "GitHub account"
        );

        service.exportVault(
                EXPORT_FILE.toString(),
                "export-password"
        );

        assertTrue(Files.exists(EXPORT_FILE));

        VaultService importedService = createService(
                "second-password"
        );

        importedService.importVault(
                EXPORT_FILE.toString(),
                "export-password"
        );

        assertEquals(
                1,
                importedService.getEntries().size()
        );

        PasswordEntry entry =
                importedService.getEntries().get(0);

        assertEquals("github.com", entry.getWebsite());
        assertEquals("user123", entry.getUsername());
        assertEquals("password123", entry.getPassword());
        assertEquals("GitHub account", entry.getNotes());
    }

    @Test
    void changeMasterPasswordUpdatesAuthenticationAndVaultKey() {
        VaultService service = createService("old-password");

        service.addEntry(
                "github.com",
                "user123",
                "password123",
                "notes"
        );

        boolean changed = service.changeMasterPassword(
                "old-password",
                "new-password"
        );

        assertTrue(changed);

        assertNull(
                MasterPassword.authenticate("old-password")
        );

        SecretKey newKey =
                MasterPassword.authenticate("new-password");

        assertNotNull(newKey);

        Vault loaded = core.storage.VaultStorage.load(
                VAULT_FILE.toString(),
                newKey
        );

        assertEquals(1, loaded.getEntries().size());

        PasswordEntry entry = loaded.getEntries().get(0);

        assertEquals("github.com", entry.getWebsite());
        assertEquals("user123", entry.getUsername());
        assertEquals("password123", entry.getPassword());
    }
}