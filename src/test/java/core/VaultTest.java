package core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class VaultTest {
    
    @Test 
    void newVaultStartsEmpty(){
        Vault vault = new Vault();

        assertTrue(vault.getEntries().isEmpty());
    }

    @Test 
    void addEntryAddsPassword(){
        Vault vault = new Vault();

        PasswordEntry entry = new PasswordEntry(
            "example.com",
            "user123", 
            "password123",
            "My notes"
        );

        vault.addEntry(entry);

        assertEquals(1, vault.getEntries().size());
        assertTrue(vault.getEntries().contains(entry));
    }

    @Test 
    void removeEntryRemovesPassword(){
        Vault vault = new Vault();

        PasswordEntry entry = new PasswordEntry(
            "example.com", 
            "user123", 
            "password123", 
            "My notes"
        );

        vault.addEntry(entry);
        vault.removeEntry(entry);

        assertTrue(vault.getEntries().isEmpty());
        assertFalse(vault.getEntries().contains(entry));
    }

    @Test 
    void searchByWebsiteFindsMatches(){
        Vault vault = new Vault();

        PasswordEntry github = new PasswordEntry(
            "github.com", 
            "user123", 
            "password123", 
            "Github account"
        );

        PasswordEntry google = new PasswordEntry(
            "google.com", 
            "user456", 
            "password456", 
            "Google account"
        );

        vault.addEntry(google);
        vault.addEntry(github);

        ArrayList<PasswordEntry> results = vault.searchByWebsite("github");

        assertEquals(1, results.size());
        assertTrue(results.contains(github));
        assertFalse(results.contains(google));
    }

    @Test
    void searchByWebsiteIsCaseInsensitive() {
        Vault vault = new Vault();

        PasswordEntry entry = new PasswordEntry(
            "GitHub.com",
            "user123",
            "password123",
            "GitHub account"
        );

        vault.addEntry(entry);

        ArrayList<PasswordEntry> results = vault.searchByWebsite("github");

        assertEquals(1, results.size());
        assertTrue(results.contains(entry));
    }

}
