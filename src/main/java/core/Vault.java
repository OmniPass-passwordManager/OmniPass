package core;

import java.util.ArrayList;

public class Vault {

    private ArrayList<PasswordEntry> entries;

    public ArrayList<PasswordEntry> searchByWebsite(String website) {

        ArrayList<PasswordEntry> results = new ArrayList<>();
        website = website.toLowerCase();

        for (PasswordEntry entry : entries) {
            String web = entry.getWebsite().toLowerCase();
            if (web.contains(website)) {
                results.add(entry);
            }

        }

        return results;
    }

    public ArrayList<PasswordEntry> getEntries() {
        return entries;
    }

    public Vault() {
        entries = new ArrayList<>();
    }

    public void addEntry(PasswordEntry entry) {
        entries.add(entry);
    }

    public void removeEntry(PasswordEntry entry) {
        entries.remove(entry);
    }

    public void displayEntries() {

        if (entries.isEmpty()) {
            System.out.println("Vault is empty.");
            return;
        }

        for (PasswordEntry entry : entries) {
            entry.display();
            System.out.println("--------------------");
        }
    }

}
