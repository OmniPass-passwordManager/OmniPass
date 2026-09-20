package core.storage;

import core.PasswordEntry;
import core.Vault;
import core.crypto.EncryptionManager;

import java.io.*;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class VaultStorage {

    private static String encode(String value){
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
    private static String decode(String value){
        return new String(Base64.getDecoder().decode(value),StandardCharsets.UTF_8);
    }

    public static Vault load(String filename, SecretKey key) {

        Vault vault = new Vault();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

            String encrypted = reader.readLine();

            if (encrypted == null || encrypted.isEmpty()) {return vault;}

            String decrypted = EncryptionManager.decrypt(encrypted, key);

            String[] lines = decrypted.split("\n");

            for (String line : lines){
                if (line.isBlank()){continue;}

                String[] parts = line.split("\\|", 4);

                if (parts.length != 4){throw new RuntimeException("Invalid entry in vault.");}

                PasswordEntry entry = new PasswordEntry(
                    decode(parts[0]),
                    decode(parts[1]),
                    decode(parts[2]),
                    decode(parts[3])
                ); 

                vault.addEntry(entry);
            }

        } catch (FileNotFoundException e) {
            System.out.println("No existing vault found. Starting with an empty vault.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load vault.", e);
        }
        return vault;
    }

    public static boolean save(Vault vault, String filename, SecretKey key) {
        try {
            File file = new File(filename);
            File parent = file.getParentFile();

            if (parent != null && !parent.exists() && !parent.mkdirs() && !parent.exists()) {
                System.out.println("Failed to create vault directory.");
                return false;
            }

            StringBuilder builder = new StringBuilder();

            for (PasswordEntry entry : vault.getEntries()) {

                builder.append(encode(entry.getWebsite()));
                builder.append("|");

                builder.append(encode(entry.getUsername()));
                builder.append("|");

                builder.append(encode(entry.getPassword()));
                builder.append("|");

                builder.append(encode(entry.getNotes()));
                builder.append("\n");
            }

            String encrypted = EncryptionManager.encrypt(builder.toString(), key);
                
            File tempFile = new File(filename + ".tmp");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {writer.write(encrypted);}
            try {
                java.nio.file.Files.move(
                    tempFile.toPath(),
                    file.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                    java.nio.file.StandardCopyOption.ATOMIC_MOVE
                    );
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                java.nio.file.Files.move(
                tempFile.toPath(),
                file.toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
        }
            return true;
        } catch (IOException e) {
            File tempFile = new File(filename + ".tmp");
            if (tempFile.exists() && !tempFile.delete()) {System.out.println("Warning: failed to remove temporary vault file.");}

            System.out.println("Failed to save vault.");
            e.printStackTrace();
            return false;
        }
    }
}