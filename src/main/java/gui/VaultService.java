package gui;

import core.PasswordEntry;
import core.Vault;
import core.storage.VaultStorage;
import core.BackupStorage;
import core.security.MasterPassword;

import java.io.*;
import java.util.ArrayList;

import java.nio.file.Files;

import javax.crypto.SecretKey;


public class VaultService {

    private static final String VAULT_FILE = "data/vault.dat";
    
    private final Vault vault;
    private SecretKey key;
    private final VaultSaver vaultSaver;

    public ArrayList<PasswordEntry> getEntries(){return vault.getEntries();}

    public ArrayList<PasswordEntry> search(String website){return vault.searchByWebsite(website);}

    public void exportVault(String filename,String exportPassword){BackupStorage.exportVault(vault, filename, exportPassword);}

    public VaultService(Vault vault, SecretKey key) {
        this(vault, key, VaultStorage::save);
    }
    
    public VaultService(Vault vault,SecretKey key,VaultSaver vaultSaver){
        this.vault = vault;
        this.key = key;
        this.vaultSaver = vaultSaver;
    }

    public void deleteEntry(PasswordEntry entry){
        if (entry == null){throw new IllegalArgumentException("Entry cannot be null.");}
        vault.removeEntry(entry);
        
        if (!vaultSaver.save(vault, VAULT_FILE, key)) {
            vault.addEntry(entry);
            throw new RuntimeException("Failed to save vault.");
        }
    }

    public void addEntry(String website, String username, String password, String notes) {
        if (website == null || website.isBlank()
                || username == null || username.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Website, username, and password are required.");
        }

        PasswordEntry entry = new PasswordEntry(website, username, password, notes);
        vault.addEntry(entry);
        
        if (!vaultSaver.save(vault, VAULT_FILE, key)) {
            vault.removeEntry(entry);
            throw new RuntimeException("Failed to save vault.");
        }
    }

    public boolean changeMasterPassword(String currentPassword, String newPassword) {
        SecretKey oldKey = MasterPassword.authenticate(currentPassword);

        if (oldKey == null) {return false;}

        MasterPassword.Credentials credentials = MasterPassword.generateCredentials(newPassword);

        SecretKey newKey = credentials.key();

        File vaultFile = new File(VAULT_FILE);
        File masterFile = new File("data/master.dat");

        byte[] oldVault = null;
        byte[] oldMaster = null;
        boolean vaultExisted = vaultFile.exists();
        boolean masterExisted = masterFile.exists();

        try {
            if (vaultExisted) {
                oldVault = Files.readAllBytes(vaultFile.toPath());
            }

            if (masterExisted) {
                oldMaster = Files.readAllBytes(masterFile.toPath());
            }

            if (!VaultStorage.save(vault, VAULT_FILE, newKey)) {
                return false;
            }

            String saltHex = core.crypto.EncryptionManager.bytesToHex(credentials.salt());

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(masterFile))) {
                writer.write(saltHex + ":" + credentials.hash());
            }

            key = newKey;
            return true;

        } catch (IOException e) {

            try {
                // Restore the previous vault state.
                if (vaultExisted) {
                    Files.write(vaultFile.toPath(), oldVault);
                } else if (vaultFile.exists() && !vaultFile.delete()) {
                    throw new IOException("Failed to remove new vault file.");
                }

                // Restore the previous master password file.
                if (masterExisted) {
                    Files.write(masterFile.toPath(), oldMaster);
                } else if (masterFile.exists() && !masterFile.delete()) {
                    throw new IOException("Failed to remove new master file.");
                }

            } catch (IOException restoreException) {
                restoreException.printStackTrace();
            }

            e.printStackTrace();
            return false;
        }
    }

    public void lock(){
        vault.clear();
        key = null;
    }

    public void deleteVault(){
        File vaultFile = new File(VAULT_FILE);
        if (vaultFile.exists() && !vaultFile.delete()){throw new RuntimeException("Failed to delete vault.");}
        vault.clear();
    }

    public void importVault(String filename,String exportPassword){
        Vault importedVault = BackupStorage.importVault(filename, exportPassword);

        ArrayList<PasswordEntry> oldEntries = vault.getEntries();
        
        vault.clear();
        vault.addEntries(importedVault.getEntries());

        if (!vaultSaver.save(vault, VAULT_FILE, key)) {
            vault.clear();
            vault.addEntries(oldEntries);

            throw new RuntimeException("Failed to save vault.");
        }
    }

    public void updateEntry(PasswordEntry entry,String website,String username,String password,String notes) {

        if (website == null || website.isBlank() || username == null || username.isBlank() || password == null || password.isBlank()) {throw new IllegalArgumentException("Website, username, and password are required.");}

        String oldWebsite = entry.getWebsite();
        String oldUsername = entry.getUsername();
        String oldPassword = entry.getPassword();
        String oldNotes = entry.getNotes();

        entry.setWebsite(website);
        entry.setUsername(username);
        entry.setPassword(password);
        entry.setNotes(notes);

        if (!vaultSaver.save(vault, VAULT_FILE, key)) {
            entry.setWebsite(oldWebsite);
            entry.setUsername(oldUsername);
            entry.setPassword(oldPassword);
            entry.setNotes(oldNotes);

            throw new RuntimeException("Failed to save vault.");
        }
    }
}