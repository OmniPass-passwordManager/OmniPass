package core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class Vault {

    private ArrayList<PasswordEntry> entries;

    public ArrayList<PasswordEntry> searchByWebsite(String website) {

        ArrayList<PasswordEntry> results = new ArrayList<>();
        website = website.toLowerCase(Locale.ROOT);

        for (PasswordEntry entry : entries) {
            String web = entry.getWebsite().toLowerCase(Locale.ROOT);
            if (web.contains(website)) {
                results.add(entry);
            }

        }

        return results;
    }

    public ArrayList<PasswordEntry> getEntries() {
        return new ArrayList<>(entries);
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

    public void clear() {
        entries.clear();
    }

    public void addEntries(Collection<PasswordEntry> newEntries) {
        entries.addAll(newEntries);
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
