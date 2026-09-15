# 🔐 OmniPass

**OmniPass** is an offline-first password manager written in **Java**.

> 🚧 OmniPass is currently under active development and is **not production-ready**.

The `develop` branch contains the current desktop GUI work built on top of the password-manager core.

## ✨ Current Features

### Password management

- Add password entries
- View saved passwords
- Search passwords by website
- Edit password entries
- Delete password entries
- Generate secure random passwords
- Choose a password length when generating a password

### Vault & security

- Master password authentication
- Salted **PBKDF2-HMAC-SHA256** password derivation
- 256-bit AES keys
- Encrypted vault storage
- Random IV generation for vault encryption
- Persistent vault save/load
- Change master password
- Re-encrypt vault when changing the master password
- Lock OmniPass and return to the login screen
- Delete/reset vault with master-password verification

### Backup & import/export

- Export encrypted vault backups
- Import encrypted vault backups
- Backup-password protection
- Restore imported entries into the active vault

### Desktop GUI

- JavaFX desktop application
- GUI master-password login
- Dark theme
- Light theme
- Theme switching
- Dashboard
- Password list UI
- Search bar
- Add password dialog
- Edit password dialog
- Settings page
- Change Master Password
- Lock OmniPass
- Import/Export controls
- Delete Vault controls

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 26 | Application language |
| Maven | Build and dependency management |
| JavaFX 26 | Desktop GUI |
| AES | Vault encryption |
| PBKDF2-HMAC-SHA256 | Master password key derivation |
| Git / GitHub | Version control |

## 📁 Project Structure

```text
OmniPass/
├── src/
│   └── main/
│       ├── java/
│       │   ├── core/
│       │   │   ├── crypto/
│       │   │   │   └── EncryptionManger.java
│       │   │   ├── security/
│       │   │   │   └── MasterPassword.java
│       │   │   ├── storage/
│       │   │   │   └── VaultStorage.java
│       │   │   ├── utils/
│       │   │   │   └── PasswordGenerator.java
│       │   │   ├── BackupStorage.java
│       │   │   ├── PasswordEntry.java
│       │   │   ├── Vault.java
│       │   │   └── Main.java
│       │   └── gui/
│       │       ├── components/
│       │       │   └── TopBar.java
│       │       ├── DashboardView.java
│       │       ├── LoginView.java
│       │       ├── MasterPasswordSetupView.java
│       │       ├── OmniPassApp.java
│       │       ├── SettingsView.java 
│       │       ├── ThemeManager.java
│       │       └── VaultService.java 
│       └── resources/
│           └── styles/
│               └── style.css
├── docs/
├── pom.xml
├── readme.md
├── todo.md
└── LICENSE
```

## 🚀 Running OmniPass

Make sure Java and Maven are installed.

### Compile

```bash
mvn compile
```

### Run the GUI

```bash
mvn javafx:run
```

The current GUI flow is:

```text
Start OmniPass
      ↓
Master Password Login
      ↓
Authenticate password
      ↓
Dashboard
```

## 🗺️ Roadmap

### v0.2.0 — Core

- [x] CLI password manager
- [x] Add / view / search passwords
- [x] Edit / delete passwords
- [x] Password generator
- [x] Master password authentication
- [x] Encrypted vault storage
- [x] Maven migration

### v0.3.0 — Desktop GUI

- [x] JavaFX application
- [x] GUI login
- [x] Dark theme
- [x] Dashboard foundation
- [x] Search bar UI
- [x] Add Password button UI
- [x] Settings button UI
- [x] Password list UI
- [x] Add password dialog
- [x] Edit password dialog
- [x] Search foundation
- [x] Settings page
- [x] Light theme
- [x] Dark theme
- [x] Theme manager
- [x] Settings functionality
- [x] Import/Export
- [x] Erase button

### v1.0.0 — Stable Release

- [ ] Complete desktop application
- [ ] Comprehensive testing
- [ ] Better error handling
- [ ] Documentation
- [ ] Packaging / distribution
- [ ] Security review
- [ ] AES-GCM migration
- [ ] Change master password
- [ ] Import/export vault
- [ ] Delete/reset vault


## ⚠️ Security Status

OmniPass is an educational and experimental project at this stage. Do not rely on it as your primary password manager for important real-world secrets yet.

The current vault and backup encryption use AES-CBC. Future security work should move the vault and backup formats to an authenticated encryption mode such as AES-GCM and include integrity protection before a stable release.

## 🤝 Contributing

This project is currently developed primarily as a personal learning project. Suggestions, bug reports, and improvements are welcome.

## 🏷️ Previous Names

- **passwd_manager** — original project name

## 📄 License

MIT License — see [`LICENSE`](LICENSE).
