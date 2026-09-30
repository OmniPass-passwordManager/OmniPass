package core;


import core.crypto.EncryptionManager;

import java.util.Base64;
import java.nio.charset.StandardCharsets;

import java.nio.file.*;
import java.io.*;
import javax.crypto.SecretKey;

public class BackupStorage {
    
    private static final String HEADER = "OMNIPASS_BACKUP_V2";

    public static String encode(String value){
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public static String decode(String value){
        return new String(
            Base64.getDecoder().decode(value),
            StandardCharsets.UTF_8
        );
    }

    public static void exportVault(Vault vault,String filename,String exportPassword){

        try{
            byte[] salt = EncryptionManager.generateSalt();

            SecretKey key = EncryptionManager.deriveKey(exportPassword, salt);

            StringBuilder builder = new StringBuilder();

            for (PasswordEntry entry : vault.getEntries()){

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

            File file = new File(filename);

            File parent = file.getParentFile();

            if (parent != null && !parent.exists()){parent.mkdirs();}

            File tempFile = new File(filename + ".tmp");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                writer.write(HEADER);
                writer.newLine();

                writer.write(EncryptionManager.bytesToHex(salt));
                writer.newLine();

                writer.write(encrypted);
            }
            try {
                Files.move(
                    tempFile.toPath(),
                    file.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(
                tempFile.toPath(),
                file.toPath(),
                StandardCopyOption.REPLACE_EXISTING
                );
            }
        } catch (IOException e){
            File tempFile = new File(filename + ".tmp");

            if (tempFile.exists() && !tempFile.delete()) {
                System.out.println("Warning: failed to remove temporary backup file");
            }

            throw new RuntimeException("Failed to export vault.", e);
        }
    }

    public static Vault importVault(String filename, String exportPassword){

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))){

            String header = reader.readLine();

            if (!HEADER.equals(header)){
                throw new RuntimeException("Invalid OmniPass backup file");
            }

            String saltHex = reader.readLine();
            String encrypted = reader.readLine();

            if (saltHex == null || encrypted == null){
                throw new RuntimeException("Invalid OmniPassword backup format.");
            }

            byte[] salt = EncryptionManager.hexToBytes(saltHex);

            SecretKey key = EncryptionManager.deriveKey(exportPassword, salt);

            String decrypted = EncryptionManager.decrypt(encrypted, key);

            Vault vault = new Vault();
            
            String[] lines = decrypted.split("\n");

            for (String line : lines){

                if (line.isBlank()){continue;}

                String[] parts = line.split("\\|", 4);

                if (parts.length != 4){
                    throw new RuntimeException("Invalid entry in backup");
                }

                PasswordEntry entry;

                entry = new PasswordEntry(
                    decode(parts[0]),
                    decode(parts[1]),
                    decode(parts[2]),
                    decode(parts[3])
                );            

                vault.addEntry(entry);
            }

            return vault;

        } catch (IOException e){
            throw new RuntimeException("Failed to import vault.",e);
        }
    }
}
