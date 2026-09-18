package gui;

import core.security.MasterPassword;

import java.io.File;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.Parent;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class SettingsView {

    private final Runnable onBack;
    private final Runnable onLock;
    private final VaultService vaultService;

    public SettingsView(VaultService vaultService,Runnable onBack,Runnable onLock) {
        this.vaultService = vaultService;
        this.onBack = onBack;
        this.onLock = onLock;
    }

    private void applyThemeToDialog(DialogPane dialogPane){ThemeManager.applyThemeToDialog(dialogPane);}

    public Parent createContent() {

        Label title = new Label("⚙ Settings");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label vaultLabel = new Label("Vault");
        vaultLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button exportButton = new Button("📤 Export Vault");
        exportButton.setOnAction(event->{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export OmniPass Vault");
            fileChooser.setInitialFileName("omnipass-backup.opb");

            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OmniPass Backup", "*.opb"));

            Window window = exportButton.getScene().getWindow();

            File file = fileChooser.showSaveDialog(window);

            if (file == null){return;}

            Dialog<String> passwordDialog = new Dialog<>();
            passwordDialog.setTitle("Export Vault");

            DialogPane dialogPane = passwordDialog.getDialogPane();

            Label titleLabel = new Label("Create an export password");
            titleLabel.setStyle("-fx-font-size: 18px;" + "-fx-font-weight: bold;");

            Label passwordLabel = new Label("Export Password:");

            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Enter a password for this backup");
            passwordField.setPrefWidth(260);
            passwordField.setMinWidth(260);
            passwordField.setPrefHeight(35);
            passwordField.setMaxHeight(35);

            HBox passwordRow = new HBox(10);
            passwordRow.setAlignment(Pos.CENTER_LEFT);
            passwordRow.getChildren().addAll(passwordLabel,passwordField);

            VBox content = new VBox(15);
            content.setPadding(new Insets(20));
            content.getChildren().addAll(titleLabel,passwordRow);

            dialogPane.setContent(content);

            ButtonType exportButtonType = new ButtonType("Export", ButtonBar.ButtonData.OK_DONE);

            dialogPane.getButtonTypes().addAll(exportButtonType,ButtonType.CANCEL);

            applyThemeToDialog(dialogPane);

            passwordDialog.setResultConverter(button -> {
                if(button == exportButtonType){return passwordField.getText();}
                return null;
            });

            passwordDialog.showAndWait().ifPresent(exportPassword ->{

                if (exportPassword.isBlank()){return;}

                try {
                    vaultService.exportVault(file.getAbsolutePath(), exportPassword);

                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    applyThemeToDialog(success.getDialogPane());

                    success.setTitle("Export Successful");
                    success.setHeaderText(null);
                    success.setContentText("Your OmniPass vault was exported successfully.");

                    success.showAndWait();
                } catch (Exception e){
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    applyThemeToDialog(error.getDialogPane());

                    error.setTitle("Export failed");
                    error.setTitle(null);
                    error.setContentText("Failed to export the vault.");

                    error.showAndWait();
                }
            
            });

        });

        Button importButton = new Button("📥 Import Vault");
        importButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();

            fileChooser.setTitle("Import OmniPass Vault");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OmniPass Backup", "*.opb"));

            Window window = importButton.getScene().getWindow();

            File file = fileChooser.showOpenDialog(window);

            if (file == null){return;}

            Dialog<String> passwordDialog = new Dialog<>();

            passwordDialog.setTitle("Import Vault");

            DialogPane dialogPane = passwordDialog.getDialogPane();

            Label titleLabel = new Label("Enter the backup password");
            titleLabel.setStyle("-fx-font-size: 18px;"+"-fx-font-weight: bold;");

            Label passwordLabel = new Label("Backup Password:");
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Enter the password for this backup");

            passwordField.setPrefWidth(260);
            passwordField.setMinWidth(260);
            passwordField.setPrefHeight(35);
            passwordField.setMaxHeight(35);

            HBox passwordRow = new HBox(10);
            passwordRow.setAlignment(Pos.CENTER_LEFT);

            passwordRow.getChildren().addAll(passwordLabel,passwordField);

            VBox content = new VBox(15);
            content.setPadding(new Insets(20));
            content.getChildren().addAll(titleLabel,passwordRow);

            dialogPane.setContent(content);

            ButtonType importButtonType = new ButtonType("Import",ButtonBar.ButtonData.OK_DONE);

            dialogPane.getButtonTypes().addAll(importButtonType,ButtonType.CANCEL);

            applyThemeToDialog(dialogPane);

            passwordDialog.setResultConverter(button ->{
                if (button == importButtonType){return passwordField.getText();}
                return null;
            });

            passwordDialog.showAndWait().ifPresent(importPassword -> {
                if (importPassword.isBlank()){return;}

                try {
                    vaultService.importVault(file.getAbsolutePath(), importPassword);

                    Alert success = new Alert(Alert.AlertType.INFORMATION);

                    applyThemeToDialog(success.getDialogPane());

                    success.setTitle("Import Successful");

                    success.setHeaderText(null);

                    success.setContentText("Your OmniPass vault was imported successfully.");

                    success.showAndWait();
                } catch (Exception e){
                    Alert error = new Alert(Alert.AlertType.ERROR);

                    applyThemeToDialog(error.getDialogPane());

                    error.setTitle("Import failed");

                    error.setHeaderText(null);

                    error.setContentText("Failed to import the vault. " + "Check the backup file and password.");

                    error.showAndWait();
                }
            });

        });

        Button deleteButton = new Button("🗑 Delete Vault");
        deleteButton.setOnAction(event -> {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);

            confirmation.setTitle("Delete Vault");
            confirmation.setHeaderText("Delete your vault?");
            confirmation.setContentText("All saved passwords will be permanently deleted.\n" + "This action cannot be undone.");

            applyThemeToDialog(confirmation.getDialogPane());

            confirmation.showAndWait().ifPresent(result -> {

                if (result != ButtonType.OK) {return;}

                Dialog<ButtonType> passwordDialog = new Dialog<>();

                passwordDialog.setTitle("Delete Vault");

                DialogPane dialogPane = passwordDialog.getDialogPane();

                Label titleLabel = new Label("Enter your OmniPass password");

                titleLabel.setStyle("-fx-font-size: 18px;" + "-fx-font-weight: bold;");

                Label passwordLabel = new Label("Master Password:");

                PasswordField passwordField = new PasswordField();

                passwordField.setPromptText("Enter your OmniPass password");

                passwordField.setPrefWidth(260);
                passwordField.setMinWidth(260);
                passwordField.setPrefHeight(35);
                passwordField.setMaxHeight(35);

                HBox passwordRow = new HBox(10);

                passwordRow.setAlignment(Pos.CENTER_LEFT);

                passwordRow.getChildren().addAll(passwordLabel,passwordField);

                VBox content = new VBox(15);

                content.setPadding(new Insets(20));

                content.getChildren().addAll(titleLabel,passwordRow);

                dialogPane.setContent(content);

                ButtonType deleteButtonType = new ButtonType("Delete",ButtonBar.ButtonData.OK_DONE);

                dialogPane.getButtonTypes().addAll(deleteButtonType,ButtonType.CANCEL);

                applyThemeToDialog(dialogPane);

                passwordDialog.showAndWait().ifPresent(button -> {

                    if (button != deleteButtonType) {return;}

                    String password = passwordField.getText();

                    if (password.isBlank()) {return;}

                    if (MasterPassword.authenticate(password) == null) {
                    Alert error = new Alert(Alert.AlertType.ERROR);

                    error.setTitle("Incorrect Password");
                    error.setHeaderText(null);
                    error.setContentText("Incorrect OmniPass password.\n" +"Your vault was not deleted.");

                    applyThemeToDialog(error.getDialogPane());

                    error.showAndWait();

                    return;
                }
            
                try {
                    vaultService.deleteVault();

                    Alert success = new Alert(Alert.AlertType.INFORMATION);

                    success.setTitle("Vault Deleted");
                    success.setHeaderText(null);
                    success.setContentText("Your OmniPass vault was deleted successfully.");

                    applyThemeToDialog(success.getDialogPane());

                    success.showAndWait();

                    onBack.run();

                } catch (Exception e) {

                    Alert error = new Alert(Alert.AlertType.ERROR);

                    error.setTitle("Delete Failed");
                    error.setHeaderText(null);
                    error.setContentText("Failed to delete the vault.");

                    applyThemeToDialog(error.getDialogPane());

                    error.showAndWait();
                    }
                });
            });
        });

        VBox vaultSection = new VBox(10);
        vaultSection.getChildren().addAll(exportButton,importButton,deleteButton);

        // Security section
        Label securityLabel = new Label("Security");
        securityLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button changePasswordButton = new Button("🔐 Change Master Password");
        changePasswordButton.setOnAction(event ->{
            Dialog<ButtonType> passwordDialog = new Dialog<>();
            passwordDialog.setTitle("Change Master Password");

            DialogPane dialogPane = passwordDialog.getDialogPane();

            Label titleLabel = new Label("Change your OmniPass master password");
            titleLabel.setStyle("-fx-font-size: 18px;" + "-fx-font-weight: bold;");

            Label currentLabel = new Label("Current Password:");

            PasswordField currentField = new PasswordField();
            currentField.setPromptText("Enter current password");

            Label newLabel = new Label("New Password");

            PasswordField newField = new PasswordField();
            newField.setPromptText("Enter new password");

            Label confirmLabel = new Label("Confirm Password:");

            PasswordField confirmField = new PasswordField();
            confirmField.setPromptText("Confirm new password");

            currentField.setPrefWidth(260);
            currentField.setMinWidth(260);

            newField.setPrefWidth(260);
            newField.setMinWidth(260);

            confirmField.setPrefWidth(260);
            confirmField.setMinWidth(260);

            VBox content = new VBox(12);
            content.setPadding(new Insets(20));

            content.getChildren().addAll(
                titleLabel,
                currentLabel,
                currentField,
                newLabel,
                newField,
                confirmLabel,
                confirmField
            );

            dialogPane.setContent(content);

            ButtonType changeButtonType = new ButtonType("Change Password",ButtonBar.ButtonData.OK_DONE);

            dialogPane.getButtonTypes().addAll(
                changeButtonType,
                ButtonType.CANCEL
            );

            applyThemeToDialog(dialogPane);

            passwordDialog.showAndWait().ifPresent(result -> {

                if (result != changeButtonType){return;}

                String currentPassword = currentField.getText();
                String newPassword = newField.getText();
                String confirmPassword = confirmField.getText();

                if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()){
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    
                    error.setTitle("Invalid Password");
                    error.setHeaderText(null);
                    error.setContentText("All password fields are required.");

                    applyThemeToDialog(error.getDialogPane());
                    error.showAndWait();
                    return;
                }

                if (!newPassword.equals(confirmPassword)){
                    Alert error = new Alert(Alert.AlertType.ERROR);

                    error.setTitle("Passwords Do Not Match.");
                    error.setHeaderText(null);
                    error.setContentText("The new password doesn't match the confirmation password.");
                    
                    applyThemeToDialog(error.getDialogPane());
                    error.showAndWait();
                    return; 
                }

                if (currentPassword.equals(newPassword)){
                    Alert error = new Alert(Alert.AlertType.ERROR);

                    error.setTitle("Invalid Password");
                    error.setHeaderText(null);
                    error.setContentText("The new password must be different from the current password.");

                    applyThemeToDialog(error.getDialogPane());
                    error.showAndWait();
                    return;
                }

                boolean changed = vaultService.changeMasterPassword(currentPassword, newPassword);

                if (!changed){
                    Alert error = new Alert(Alert.AlertType.ERROR);

                    error.setTitle("Password Change Failed");
                    error.setHeaderText(null);
                    error.setContentText("The current password is incorrect or the password change failed.");

                    applyThemeToDialog(error.getDialogPane());
                    error.showAndWait();
                    return;
                }
                
                Alert success = new Alert(Alert.AlertType.INFORMATION);

                success.setTitle("Password Changed");
                success.setHeaderText(null);
                success.setContentText("Your OmniPass master password was changed successfully.");

                applyThemeToDialog(success.getDialogPane());

                success.showAndWait();
            });
        });

        Button lockButton = new Button("🔒 Lock OmniPass");
        lockButton.setOnAction(event ->{
            vaultService.lock();
            onLock.run();
        });

        VBox securitySection = new VBox(10);
        securitySection.getChildren().addAll(changePasswordButton,lockButton);

        Label appearanceLabel = new Label("Appearance");
        appearanceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label themeLabel = new Label("Theme");

        ComboBox<String> themeBox = new ComboBox<>();
        themeBox.getItems().addAll("Light", "Dark");
        themeBox.setValue(ThemeManager.getCurrentTheme());
        themeBox.setPrefWidth(120);
        themeBox.setMinWidth(120);

        HBox themeRow = new HBox(15);
        themeRow.setAlignment(Pos.CENTER_LEFT);
        themeRow.getChildren().addAll(themeLabel, themeBox);

        Label aboutLabel = new Label("About");
        aboutLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button aboutButton = new Button("ℹ About OmniPass");

        Button backButton = new Button("<- Back");
        backButton.setOnAction(event -> onBack.run());

        Separator separator = new Separator();

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));

        root.getChildren().addAll(title,separator,vaultLabel,vaultSection,securityLabel,securitySection,appearanceLabel,themeRow,aboutLabel,aboutButton,backButton);

        themeBox.setOnAction(event -> {
            String selectedTheme = themeBox.getValue();

            ThemeManager.setTheme(selectedTheme);
            ThemeManager.applyThemeToRoot(root, selectedTheme);
        });

        ThemeManager.applyThemeToRoot(root,ThemeManager.getCurrentTheme());
        return root;
    }
}