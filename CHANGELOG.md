# Changelog

All notable changes to OmniPass are documented here.

## [Unreleased]

### Security
- Hardened master password authentication against malformed and empty `master.dat` files.
- Added validation for hexadecimal data used by the security system.
- Improved master password change handling with restoration of the previous vault and master password data if an update fails.
- Updated `EncryptionManager.hash()` to explicitly use UTF-8 encoding.
- Added Unicode hashing tests.
- Added additional encryption and security validation tests.

### Core
- Made website search case-insensitive.
- Added validation tests for hexadecimal conversion.
- Added Unicode hash testing.

### GUI
- Fixed login stylesheet loading.
- Corrected various UI spelling and grammar errors.
- Cleaned up master password creation handling.

### Testing
- Expanded the test suite from **25 tests to 31 tests**.
- Current test status: **31/31 passing**.

### Maintenance
- Improved code readability in several areas.