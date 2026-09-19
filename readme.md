# 🔐 OmniPass

**OmniPass** is an offline-first password manager written in **Java**.

> 🚧 OmniPass is currently under active development and is **not production-ready**.

The `develop` branch contains the current desktop GUI and password-manager core.

## ✨ Current Features

### Password Management

- Add password entries
- View saved passwords
- Search passwords by website
- Case-insensitive website search
- Edit password entries
- Delete password entries
- Secure random password generation
- Configurable password length

### Vault & Security

- Master password authentication
- Salted PBKDF2-HMAC-SHA256 key derivation
- 256-bit AES keys
- AES-256-GCM authenticated vault encryption
- Random encryption nonces
- Encrypted persistent vault storage
- Change master password
- Automatic vault re-encryption when changing the master password
- Lock OmniPass
- Delete/reset vault with master-password verification
- Input validation for security-related data

### Backup & Import/Export

- Export encrypted vault backups
- Import encrypted vault backups
- Separate backup password protection
- Random backup encryption salt
- Restore imported entries into the active vault
- Backup format validation

### Desktop GUI

- JavaFX desktop application
- Master-password setup screen
- Master-password login
- Dashboard
- Password list
- Search bar
- Add password dialog
- Edit password dialog
- Password details
- Clipboard password copying
- Settings page
- Import/export controls
- Change master password
- Lock OmniPass
- Delete vault
- Light theme
- Dark theme
- Theme switching
- About OmniPass dialog

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 26 | Application language |
| Maven | Build and dependency management |
| JavaFX 26 | Desktop GUI |
| JUnit | Automated testing |
| AES-256-GCM | Authenticated vault/backup encryption |
| PBKDF2-HMAC-SHA256 | Password-based key derivation |
| Git / GitHub | Version control |

## 📁 Project Structure

```text
OmniPass/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── core/
│   │   │   │   ├── crypto/
│   │   │   │   │   └── EncryptionManager.java
│   │   │   │   ├── security/
│   │   │   │   │   └── MasterPassword.java
│   │   │   │   ├── storage/
│   │   │   │   │   └── VaultStorage.java
│   │   │   │   ├── utils/
│   │   │   │   │   └── PasswordGenerator.java
│   │   │   │   ├── BackupStorage.java
│   │   │   │   ├── PasswordEntry.java
│   │   │   │   ├── Vault.java
│   │   │   │   └── Main.java
│   │   │   └── gui/
│   │   │       ├── components/
│   │   │       │   └── TopBar.java
│   │   │       ├── DashboardView.java
│   │   │       ├── LoginView.java
│   │   │       ├── MasterPasswordSetupView.java
│   │   │       ├── OmniPassApp.java
│   │   │       ├── SettingsView.java
│   │   │       ├── ThemeManager.java
│   │   │       └── VaultService.java
│   │   └── resources/
│   │       └── styles/
│   │           └── style.css
│   └── test/
│       └── java/
│           ├── core/
│           │   ├── BackupStorageTest.java
│           │   ├── PasswordEntryTest.java
│           │   ├── VaultTest.java
│           │   ├── crypto/
│           │   │   └── EncryptionManagerTest.java
│           │   ├── security/
│           │   │   └── MasterPasswordTest.java
│           │   └── storage/
│           │       └── VaultStorageTest.java
│           └── gui/
│               └── VaultServiceTest.java
├── pom.xml
├── readme.md
└── LICENSE
```

## 🚀 Running OmniPass

Make sure Java 26 and Maven are installed.

### Compile

```bash
mvn compile
```

### Run the GUI

```bash
mvn javafx:run
```

### Run tests

```bash
mvn clean test
```

The current test suite contains **52 automated tests**.

## 🔐 Encryption

OmniPass currently uses:

- **AES/GCM/NoPadding**
- 256-bit AES keys
- 12-byte random GCM nonces
- 128-bit GCM authentication tags
- **PBKDF2WithHmacSHA256**
- 65,536 PBKDF2 iterations
- 16-byte random salts
- UTF-8 encoding

Vault and backup data are encrypted before being written to disk.

## 🗺️ Roadmap

### v0.2.0 — Core & GUI

- [x] CLI password manager
- [x] Add / view / search passwords
- [x] Edit / delete passwords
- [x] Password generator
- [x] Master password authentication
- [x] Encrypted vault storage
- [x] JavaFX desktop application
- [x] Dashboard
- [x] Settings
- [x] Light / dark themes
- [x] Import / export
- [x] Change master password
- [x] Lock OmniPass
- [x] Delete/reset vault
- [x] AES-GCM encryption
- [x] Automated test suite

### Future Work

- [ ] Improve error handling
- [ ] Persistent theme preference
- [ ] More comprehensive GUI testing
- [ ] Packaging / distribution
- [ ] Security review
- [ ] Additional usability improvements

## ⚠️ Security Status

OmniPass is an educational and experimental project and is **not production-ready**.

Although the vault and backup systems use authenticated AES-GCM encryption and password-based key derivation, the project has not undergone a professional security audit.

Do not use OmniPass as your primary password manager for important real-world secrets.

## 🤝 Contributing

OmniPass is currently developed primarily as a personal learning project. Suggestions, bug reports, and improvements are welcome.

## 🏷️ Previous Names

- **passwd_manager** — original project name

## 📄 License

MIT License — see [`LICENSE`](LICENSE).