package core.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.crypto.SecretKey;

import core.crypto.EncryptionManager;

public class MasterPassword {

    private static final String MASTER_FILE = "data/master.dat";

    public record Credentials(byte[] salt, String hash, SecretKey key) {}

    public static Credentials generateCredentials(String password){
        byte[] salt = EncryptionManager.generateSalt();
        String hash = EncryptionManager.pbkdf2Hash(password, salt);
        SecretKey key = EncryptionManager.deriveKey(password, salt);

        return new Credentials(salt, hash, key);
    }

    private static Boolean generator(String password){

        try (FileWriter writer = new FileWriter(MASTER_FILE)) {

            byte[] salt = EncryptionManager.generateSalt();

            String saltHex = EncryptionManager.bytesToHex(salt);

            String hash = EncryptionManager.pbkdf2Hash(password, salt);

            writer.write(saltHex + ":" + hash);

            return true;

        } catch (FileNotFoundException e){
            System.out.println("file not there");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }    

    public static boolean exists(){
        return new File(MASTER_FILE).exists();
    }

    public static SecretKey cli(Scanner scanner) {

        System.out.print("Enter Master Password: ");
        String enteredPassword = scanner.nextLine();

        SecretKey key = authenticate(enteredPassword);

        if (key != null) {
            System.out.println("Access Granted!");
        } else {
            System.out.println("Incorrect Master Password.");
        }

        return key;
    }

    public static void create(Scanner scanner) {

        while (true) {

            System.out.print("Create Master Password: ");
            String password = scanner.nextLine();

            System.out.print("Confirm Master Password: ");
            String confirm = scanner.nextLine();

            if (!password.equals(confirm)) {
                System.out.println("Passwords do not match!\n");
                continue;
            }

            boolean created = generator(password);

            if (!created){
                System.out.println("Master password was not created");
                return;
            }
            else{
                System.out.println("Master password created successfully!");
                return;
            }
        }

    }

    public static void create(String password){

        boolean created = generator(password);
        if (created) {}
        return;
    }

    public static SecretKey authenticate(String enteredPassword){
        try(BufferedReader reader = new BufferedReader(new FileReader(MASTER_FILE))) {

            String line = reader.readLine();

            if (line == null || line.isBlank()){
                System.out.println("Invalid master password file");
                return null;
            }

            String[] parts = line.split(":",-1);

            if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()){
                System.out.println("Invalid master password file");
                return null;
            }

            String saltHex = parts[0];
            String savedHash = parts[1];

            byte[] salt;

            try {
                salt = EncryptionManager.hexToBytes(saltHex);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid master password file");
                return null;
            }

            if (salt.length != 16) {
                System.out.println("Invalid master password file");
                return null;
            }

            String enteredHash = EncryptionManager.pbkdf2Hash(enteredPassword,salt);

            if (savedHash.equals(enteredHash)) {
                return EncryptionManager.deriveKey(enteredPassword, salt);
            }

            return null;
            
        } catch (FileNotFoundException e) {
            System.out.println("Failed to read master password.");
            return null;
        } catch (IOException e) {
            System.out.println("Failed to read master password.");
            e.printStackTrace();
            return null;
        }
    }
}
