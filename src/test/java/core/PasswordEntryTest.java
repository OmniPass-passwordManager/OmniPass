package core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordEntryTest {
    
    @Test
    void constructorStoresValues(){
        PasswordEntry entry = new PasswordEntry(
            "example.com", 
            "user123", 
            "password123", 
            "My notes"
        );

        assertEquals("example.com", entry.getWebsite());
        assertEquals("user123", entry.getUsername());
        assertEquals("password123", entry.getPassword());
        assertEquals("My notes", entry.getNotes());
    }

    @Test 
    void settersUpdateValues(){
        PasswordEntry entry = new PasswordEntry(
            "oldWebsite", 
            "oldUser", 
            "oldPassword", 
            "oldNotes"
        );

        entry.setWebsite("newWebsite");
        entry.setUsername("newUser");
        entry.setPassword("newPassword");
        entry.setNotes("newNotes");

        assertEquals("newWebsite", entry.getWebsite());
        assertEquals("newUser", entry.getUsername());
        assertEquals("newPassword", entry.getPassword());
        assertEquals("newNotes", entry.getNotes());
    }

    @Test 
    void settersAllowEmptyValues(){
        PasswordEntry entry = new PasswordEntry(
            "website", 
            "username",
            "password",
            "notes"
        );

        entry.setWebsite("");
        entry.setUsername("");
        entry.setPassword("");
        entry.setNotes("");

        assertEquals("", entry.getWebsite());
        assertEquals("", entry.getUsername());
        assertEquals("", entry.getPassword());
        assertEquals("", entry.getNotes());
    }   
}
