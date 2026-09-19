package core;

import java.util.Scanner;

import javax.crypto.SecretKey;

import java.util.ArrayList;
import java.io.File;

import core.security.MasterPassword;
import core.storage.VaultStorage;
import core.utils.PasswordGenerator;

public class Main {

    private static final String VAULT_FILE = "data/vault.dat";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        File masterFile = new File("data/master.dat");
        if (!masterFile.exists()) {
            MasterPassword.create(scanner);
        }

        SecretKey key = MasterPassword.cli(scanner);

        if (key == null) {
            scanner.close();
            return;

        }

        Vault vault = VaultStorage.load(VAULT_FILE, key);

        while (true) {
            printMenu();
            System.out.print("Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1: {
                    addPassword(scanner, vault, key);
                    break;
                }

                case 2: {
                    vault.displayEntries();
                    break;
                }

                case 3: {
                    searchPassword(scanner, vault);
                    break;
                }

                case 4: {
                    editPassword(scanner, vault, key);
                    break;
                }

                case 5: {
                    VaultStorage.save(vault, VAULT_FILE, key);
                    break;
                }

                case 6: {
                    deletePassword(scanner, vault, key);
                    break;
                }

                case 7: {
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;
                }

                default: {
                    System.out.println("Invalid choice");
                }
            }
        }

    }

    private static void printMenu() {
        System.out.println("\n====================");
        System.out.println("      PasswdManager");
        System.out.println("====================");
        System.out.println("1. Add Password");
        System.out.println("2. View Passwords");
        System.out.println("3. Search Password");
        System.out.println("4. Edit Password");
        System.out.println("5. Save Vault");
        System.out.println("6. Delete Password");
        System.out.println("7. Exit");
    }

    private static PasswordEntry selectEntry(Scanner scanner, Vault vault) {
        System.out.print("Website: ");
        String website = scanner.nextLine();

        ArrayList<PasswordEntry> results = vault.searchByWebsite(website);

        if (results.isEmpty()) {
            System.out.println("No matching Websites found.");
            return null;
        }

        int i = 1;
        for (PasswordEntry entry : results) {
            System.out.println(i + ".");
            entry.displaySummary();
            System.out.println("--------------------");
            i++;
        }

        System.out.print("Choose Entry: ");
        int selected = scanner.nextInt();
        scanner.nextLine();

        if (selected < 1 || selected > results.size()) {
            System.out.println("Invalid selection.");
            return null;
        }
        return results.get(selected - 1);
    }

    private static void deletePassword(Scanner scanner, Vault vault, SecretKey key) {

        PasswordEntry entry = selectEntry(scanner, vault);

        System.out.print("Are you sure (y/n): ");
        String Decision = scanner.nextLine();

        if (Decision.equalsIgnoreCase("y")) {
            vault.removeEntry(entry);
        } else if (Decision.equalsIgnoreCase("n")) {
            System.out.println("Permission denied");
        } else {
            System.out.println("Unknown decision");
        }
        VaultStorage.save(vault, VAULT_FILE, key);
    }

    private static void editPassword(Scanner scanner, Vault vault, SecretKey key) {

        PasswordEntry entry = selectEntry(scanner, vault);

        if (entry == null) {
            return;
        }

        System.out.print("New Website: ");
        String newWebsite = scanner.nextLine();
        entry.setWebsite(newWebsite);

        System.out.print("New Username: ");
        String newUsername = scanner.nextLine();
        entry.setUsername(newUsername);

        System.out.print("New Password: ");
        String newPassword = scanner.nextLine();
        entry.setPassword(newPassword);

        System.out.print("New Notes: ");
        String newNotes = scanner.nextLine();
        entry.setNotes(newNotes);

        VaultStorage.save(vault, VAULT_FILE, key);
    }

    private static void searchPassword(Scanner scanner, Vault vault) {
        System.out.print("Website: ");
        String website = scanner.nextLine();

        ArrayList<PasswordEntry> results = vault.searchByWebsite(website);

        if (results.isEmpty()) {
            System.out.println("No matching Websites found.");
        } else {
            for (PasswordEntry entry : results) {
                entry.display();
                System.out.println("--------------------");
            }
        }
    }

    private static void addPassword(Scanner scanner, Vault vault, SecretKey key) {
        System.out.print("Website: ");
        String website = scanner.nextLine();

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Generate a secure password(y/n): ");
        String choice = scanner.nextLine();

        String password;

        if (choice.equalsIgnoreCase("y")) {
            int length = 20;

            while (true) {

                System.out.print("Password Length (default: 20): ");
                String input = scanner.nextLine();

                if (input.isBlank()) {
                    break;
                }
                try {
                    length = Integer.parseInt(input);

                    if (length < 4) {
                        System.out.println("Password must be at least 4 characters.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }

            while (true) {
                password = PasswordGenerator.generate(length);

                System.out.println("\nGenerated Password: ");
                System.out.println(password);

                System.out.println("\n1. Use password");
                System.out.println("2. Generate Another");
                System.out.println("3. Enter Manualy");

                System.out.println("Choice: ");
                String option = scanner.nextLine();

                if (option.equals("1")) {
                    break;
                }

                if (option.equals("2")) {
                    continue;
                }

                if (option.equals("3")) {
                    System.out.print("Password: ");
                    password = scanner.nextLine();
                    break;
                }
            }

        } else {
            System.out.print("Password: ");
            password = scanner.nextLine();
        }

        System.out.print("Notes: ");
        String notes = scanner.nextLine();

        PasswordEntry entry = new PasswordEntry(website, username, password, notes);
        vault.addEntry(entry);

        System.out.println("Password added successfully!");
        VaultStorage.save(vault, VAULT_FILE, key);
    }
}
