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

    public ArrayList<PasswordEntry> getEntries(){return vault.getEntries();}

    public ArrayList<PasswordEntry> search(String website){return vault.searchByWebsite(website);}

    public void exportVault(String filename,String exportPassword){BackupStorage.exportVault(vault, filename, exportPassword);}

    public VaultService(Vault vault,SecretKey key){
        this.vault = vault;
        this.key = key;
    }

    public void deleteEntry(PasswordEntry entry){
        vault.removeEntry(entry);
        VaultStorage.save(vault, VAULT_FILE, key);
    }

    public void addEntry(String website,String username,String password,String notes){
        PasswordEntry entry = new PasswordEntry(website, username, password, notes);
        vault.addEntry(entry);
        VaultStorage.save(vault, VAULT_FILE, key);
    }

    public boolean changeMasterPassword(String currentPassword, String newPassword){
        SecretKey oldKey = MasterPassword.authenticate(currentPassword);

        if (oldKey == null){return false;}

        MasterPassword.Credentials credentials = MasterPassword.generateCredentials(newPassword);

        SecretKey newKey = credentials.key();

        File vaultFile = new File(VAULT_FILE);
        File masterFile = new File("data/master.dat");

        byte[] oldVault = null;
        byte[] oldMaster = null;

        try {
            if (vaultFile.exists()){
                oldVault = Files.readAllBytes(vaultFile.toPath());
            }

            if (masterFile.exists()) {
                oldMaster = Files.readAllBytes(masterFile.toPath());
            }

            if (!VaultStorage.save(vault, VAULT_FILE, newKey)){
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
                if (oldVault != null) {
                    Files.write(vaultFile.toPath(), oldVault);
                }

                if (oldMaster != null) {
                    Files.write(masterFile.toPath(), oldMaster);
                }

            } catch (IOException restoreException) {
                restoreException.printStackTrace();
            }

            e.printStackTrace();
            return false;
        }
    }

    public void lock(){
        vault.getEntries().clear();
        key = null;
    }

    public void deleteVault(){
        vault.getEntries().clear();
        File vaultFile = new File(VAULT_FILE);
        if (vaultFile.exists() && !vaultFile.delete()){throw new RuntimeException("Failed to delete vault.");}
    }

    public void importVault(String filename,String exportPassword){
        Vault importedVault = BackupStorage.importVault(filename, exportPassword);
        vault.getEntries().clear();
        vault.getEntries().addAll(importedVault.getEntries());
        VaultStorage.save(vault, VAULT_FILE, key);
    }

    public void updateEntry(PasswordEntry entry,String website,String username,String password,String notes){
        entry.setWebsite(website);
        entry.setUsername(username);
        entry.setPassword(password);
        entry.setNotes(notes);
        VaultStorage.save(vault, VAULT_FILE, key);
    }

}
