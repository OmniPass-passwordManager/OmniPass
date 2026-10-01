package gui;

import core.Vault;

import javax.crypto.SecretKey;

@FunctionalInterface 
public interface VaultSaver {
    boolean save(Vault vault, String filename, SecretKey key);
} 
