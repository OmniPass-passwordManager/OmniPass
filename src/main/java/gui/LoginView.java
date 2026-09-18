package gui;

import core.Vault;
import core.security.MasterPassword;
import core.storage.VaultStorage;

import javafx.geometry.Pos;

import javafx.stage.Stage;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import javax.crypto.SecretKey;

public class LoginView {

    private PasswordField passwordField = new PasswordField();

    private void login(Stage stage){
        String password = getPassword();

        if (password.isBlank()){
            showError("Please enter your master password");
            return;
        }

        SecretKey key = MasterPassword.authenticate(password);

        if (key == null){
            showError("Incorrect master password.");
            return;
        }

        Vault vault;

        try {
            vault = VaultStorage.load("data/vault.dat", key);
        } catch (RuntimeException e){
            showError("Vault cound not be verified. It may be corrupted or modified.");
            return;
        }

        if (vault == null){
            vault = new Vault();
        }

        VaultService vaultService = new VaultService(vault, key);
        LoginView loginView = new LoginView();

        DashboardView dashboard = new DashboardView(vaultService, () ->{
            Parent loginContent = loginView.createContent(stage);
            Scene loginScene = new Scene(loginContent, 900, 600);

            loginScene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            
            ThemeManager.applyTheme(loginScene, ThemeManager.getCurrentTheme());

            stage.setScene(loginScene);
        });

        Scene dashboardScene = new Scene(dashboard.createContent(), 900, 600);
        dashboardScene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        ThemeManager.applyTheme(dashboardScene, ThemeManager.getCurrentTheme());
        stage.setScene(dashboardScene);
    }   


    private void showError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Login failed");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public Parent createContent(Stage stage) {

        Label title = new Label("🔐 OmniPass");

        Label passwordLabel = new Label("Master Password");

        passwordField.setMaxWidth(250);
        passwordField.setPromptText("Enter master password");

        Button loginButton = new Button("Unlock");
        loginButton.setMaxWidth(250);
        loginButton.setOnAction(event -> login(stage));

        passwordField.setOnAction(event -> login(stage));

        VBox root = new VBox(20);

        root.setAlignment(Pos.CENTER);
        root.setSpacing(20);

        root.getChildren().addAll(title, passwordLabel, passwordField, loginButton);

        return root;
    }

    public String getPassword() {
        return passwordField.getText();
    }
}
