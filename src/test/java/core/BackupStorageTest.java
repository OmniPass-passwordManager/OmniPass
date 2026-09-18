package core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class BackupStorageTest {
    
    private final Path backupFile = Path.of("data/test-backup.opd");

    @AfterEach 
    void cleanup() throws Exception {
        Files.deleteIfExists(backupFile);
    }

    @Test 
    void exportAndImportVault() {
        Vault vault = new Vault();

        vault.addEntry(new PasswordEntry(
            "github.com", 
            "user123", 
            "password123", 
            "GitHub account"
        ));

        BackupStorage.exportVault(vault, backupFile.toString(), "export-password");

        Vault imported = BackupStorage.importVault(backupFile.toString(), "export-password");

        assertEquals(1, imported.getEntries().size());

        PasswordEntry entry = imported.getEntries().get(0);

        assertEquals("github.com", entry.getWebsite());
        assertEquals("user123", entry.getUsername());
        assertEquals("password123", entry.getPassword());
        assertEquals("GitHub account", entry.getNotes());
    }

    @Test 
    void multipleEntriesRoundTrip() {
        Vault vault = new Vault();

        vault.addEntry(new PasswordEntry("github.com", "user1", "pass1", "notes1"));

        vault.addEntry(new PasswordEntry("google.com", "user2", "pass2", "notes2"));

        BackupStorage.exportVault(vault, backupFile.toString(), "export-password");

        Vault imported = BackupStorage.importVault(backupFile.toString(), "export-password");

        assertEquals(2, imported.getEntries().size());
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

        BackupStorage.exportVault(vault, backupFile.toString(), "export-password");

        Vault imported = BackupStorage.importVault(backupFile.toString(), "export-password");

        PasswordEntry entry = imported.getEntries().get(0);

        assertEquals("例え.テスト", entry.getWebsite());
        assertEquals("ユーザー 🔐", entry.getUsername());
        assertEquals("пароль", entry.getPassword());
        assertEquals("नमस्ते 🌍", entry.getNotes());
    }

    @Test
    void wrongPasswordFails() {
        Vault vault = new Vault();

        vault.addEntry(new PasswordEntry("github", "user123", "secret", "notes"));

        BackupStorage.exportVault(vault, backupFile.toString(), "correct-password");

        assertThrows(
            RuntimeException.class, 
            () -> BackupStorage.importVault(backupFile.toString(), "wrong-password")
        );
    }

    @Test 
    void invalidHeaderFails() throws Exception {
        Files.writeString(
            backupFile, 
            "INVALID_HEADER\n" + "00112233445566778899aabbccddeeff\n" + "anything"
        );

        assertThrows(
            RuntimeException.class, 
            () -> BackupStorage.importVault(backupFile.toString(), "password")
        );
    }

    @Test
    void backupDoesNotContainPlaintextData() throws Exception {
        Vault vault = new Vault();

        String password = "SuperSecretPassword123!";
        String notes = "Private backup notes";

        vault.addEntry(new PasswordEntry(
                "github.com",
                "user123",
                password,
                notes
        ));

        BackupStorage.exportVault(
                vault,
                backupFile.toString(),
                "export-password"
        );

        String fileContents = Files.readString(backupFile);

        assertTrue(
                fileContents.startsWith("OMNIPASS_BACKUP_V2")
        );

        assertFalse(fileContents.contains(password));
        assertFalse(fileContents.contains(notes));
    }

    @Test
    void missingBackupFails() {
        assertThrows(
                RuntimeException.class,
                () -> BackupStorage.importVault(
                        backupFile.toString(),
                        "password"
                )
        );
    }
}
