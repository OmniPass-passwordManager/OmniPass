package gui;

import core.security.MasterPassword;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class MasterPasswordSetupView {
    
    private PasswordField passwordField = new PasswordField();
    private PasswordField confirmField = new PasswordField();

    private void createPassword(Stage stage){

        String password = passwordField.getText();
        String confirm = confirmField.getText();

        if (password.isBlank() || confirm.isBlank()){
            showError("Please fill in both password fields");
            return;
        }

        if (!password.equals(confirm)){
            showError("Passwords do not match.");
            return;
        }

        MasterPassword.create(password);

        LoginView loginView = new LoginView();

        Scene loginScene = new Scene(
            loginView.createContent(stage),
            900,
            600
        );

        loginScene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        ThemeManager.applyTheme(loginScene, ThemeManager.getCurrentTheme());

        stage.setScene(loginScene);
    }

    private void showError(String message){

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Master password");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public Parent createcontent(Stage stage){

        Label title = new Label("🔐OmniPass");

        Label subtitle = new Label("Create your master password");

        passwordField.setMaxWidth(250);
        passwordField.setPromptText("Master password");

        confirmField.setMaxWidth(250);
        confirmField.setPromptText("Confirm password");

        Button createButton = new Button("Create Master Password");

        createButton.setMaxWidth(250);

        createButton.setOnAction(event -> createPassword(stage));

        VBox root = new VBox(20);

        root.setAlignment(Pos.CENTER);
        root.setSpacing(20);

        root.getChildren().addAll(title,subtitle,passwordField,confirmField,createButton);

        return root;
    }
}
