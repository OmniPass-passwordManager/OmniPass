package core;

public class PasswordEntry {

    String website;
    String username;
    String password;
    String notes;

    public PasswordEntry(String website, String username, String password, String notes) {
        this.website = website;
        this.username = username;
        this.password = password;
        this.notes = notes;
    }

    public void display() {
        System.out.println("Website: " + website);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Notes: " + notes);
    }

    public void displaySummary() {
        System.out.println(getWebsite());
        System.out.println(getUsername());
    }

    // Getters
    public String getWebsite() {
        return website;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNotes() {
        return notes;
    }

    // setter
    public void setUsername(String username) {
            this.username = username;
    }

    public void setPassword(String password) {
            this.password = password;
    }

    public void setNotes(String notes) {
            this.notes = notes;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
