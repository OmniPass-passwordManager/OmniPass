package gui;

import gui.components.TopBar;

import core.PasswordEntry;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class DashboardView {

    private BorderPane root;
    private VBox passwordCenter;

    private ListView<PasswordEntry> passwordList;

    private final VaultService vaultService;
    private final Runnable onLock;

    public DashboardView(VaultService vaultService,Runnable onLock){
        this.vaultService = vaultService;
        this.onLock = onLock;
    }
    
    private void applyDialogTheme(Dialog<?> dialog){
        DialogPane dialogPane = dialog.getDialogPane();
        String css = getClass().getResource("/styles/style.css").toExternalForm();
        if (!dialogPane.getStylesheets().contains(css)){dialogPane.getStylesheets().add(css);}
        ThemeManager.applyThemeToRoot(dialogPane,ThemeManager.getCurrentTheme());
    }

    private void showAddPasswordDialog(ListView<PasswordEntry> passwordList){

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Add Password");
        dialog.setHeaderText("Add a new password");

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        TextField websitesField = new TextField();
        websitesField.setPromptText("Website");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        TextArea notesField = new TextArea();
        notesField.setPromptText("Notes");
        notesField.setPrefRowCount(3);

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Website:"), 0, 0);
        grid.add(websitesField, 1, 0);

        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);

        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);

        grid.add(new Label("Notes:"), 0, 3);
        grid.add(notesField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {

            if (button == saveButton){
                String website = websitesField.getText();
                String username = usernameField.getText();
                String password = passwordField.getText();
                String notes = notesField.getText();

                if (website.isBlank()|| username.isBlank()|| password.isBlank()){return null;}

                vaultService.addEntry(website,username,password,notes);

                passwordList.getItems().setAll(vaultService.getEntries());
            }

            return button;
        });

        applyDialogTheme(dialog);
        dialog.showAndWait();
    }

    private void showEditPasswordDialog(PasswordEntry entry,ListView<PasswordEntry> passwordList){

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Edit Password");
        dialog.setHeaderText("Edit password");

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        TextField websiteField = new TextField(entry.getWebsite());
        TextField usernameField = new TextField(entry.getUsername());
        PasswordField passwordField = new PasswordField();
        TextArea notesField = new TextArea(entry.getNotes());

        websiteField.setPromptText("Website");
        usernameField.setPromptText("Username");
        passwordField.setPromptText("Password");
        notesField.setPromptText("Notes");
        notesField.setPrefRowCount(3);

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Website:"), 0, 0);
        grid.add(websiteField, 1, 0);

        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);

        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);

        grid.add(new Label("Notes:"), 0, 3);
        grid.add(notesField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {

            if (button == saveButton){

                String website = websiteField.getText();
                String username = usernameField.getText();
                String password = passwordField.getText();
                String notes = notesField.getText();

                if (website.isBlank() || username.isBlank()){
                    if (password.isBlank()){password = entry.getPassword();}
                    return null;
                }

                vaultService.updateEntry(entry,website,username,password,notes);

                passwordList.getItems().setAll(vaultService.getEntries());}

            return button;
        });

        applyDialogTheme(dialog);
        dialog.showAndWait();
    }

    private void showPasswordDetails(PasswordEntry entry,ListView<PasswordEntry> passwordList){

        BorderPane detailsRoot = new BorderPane();
        detailsRoot.setPadding(new Insets(20));

        Button backButton = new Button("<-");

        Label title = new Label(entry.getWebsite());
        title.setStyle("-fx-font-size: 22px;"+ "-fx-font-weight: bold;");

        HBox header = new HBox(15, backButton, title);
        header.setAlignment(Pos.CENTER_LEFT);

        backButton.setOnAction(event -> {root.setCenter(passwordCenter);});

        Label usernameTitle = new Label("Username");
        usernameTitle.setStyle("-fx-font-size: 13px");

        Label usernameValue = new Label(entry.getUsername());
        usernameValue.setStyle("-fx-font-size: 16px");

        Button copyUsernameButton = new Button("📋");

        copyUsernameButton.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();

            content.putString(entry.getUsername());
            clipboard.setContent(content);
        });

        HBox usernameHeader = new HBox(10,usernameTitle,new Region(),copyUsernameButton);
        HBox.setHgrow(usernameHeader.getChildren().get(1),Priority.ALWAYS);

        VBox usernameBox = new VBox(8,usernameHeader,usernameValue);
        usernameBox.setPadding(new Insets(15));

        Label passwordTitle = new Label("Password");
        passwordTitle.setStyle("-fx-font-size: 13px;");

        PasswordField hiddenPassword = new PasswordField();
        hiddenPassword.setText(entry.getPassword());
        hiddenPassword.setEditable(false);

        TextField visiblePassword = new TextField(entry.getPassword());
        visiblePassword.setEditable(false);
        visiblePassword.setVisible(false);
        visiblePassword.setManaged(false);

        Button showPasswordButton = new Button("👁");
        Button copyPasswordButton = new Button("📋");

        showPasswordButton.setOnAction(event -> {
            boolean showing = visiblePassword.isVisible();

            visiblePassword.setVisible(!showing);
            visiblePassword.setManaged(!showing);

            hiddenPassword.setVisible(showing);
            hiddenPassword.setManaged(showing);
        });


        copyPasswordButton.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(entry.getPassword());
            clipboard.setContent(content);
        });

        HBox passwordButtons = new HBox(8,showPasswordButton,copyPasswordButton);

        Region passwordSpacer = new Region();

        HBox passwordHeader = new HBox(10,passwordTitle,passwordSpacer,passwordButtons);

        HBox.setHgrow(passwordSpacer,Priority.ALWAYS);

        VBox passwordBox = new VBox(8,passwordHeader,hiddenPassword,visiblePassword);

        passwordBox.setPadding(new Insets(15));

        Label notesTitle = new Label("Notes");
        notesTitle.setStyle("-fx-font-size: 13px;");

        String notes = entry.getNotes();

        Label notesValue = new Label(notes == null || notes.isBlank()? "No notes added": notes);
        notesValue.setWrapText(true);
        notesValue.setStyle("-fx-font-size: 15px;");

        VBox notesBox = new VBox(8,notesTitle,notesValue);
        notesBox.setPadding(new Insets(15));

        VBox content = new VBox(15,usernameBox,passwordBox,notesBox);

        VBox.setVgrow(notesBox,Priority.NEVER);

        detailsRoot.setTop(header);
        detailsRoot.setCenter(content);

        Button editButton = new Button("Edit");

        Button deleteButton = new Button("Delete");

        editButton.setOnAction(event -> {showEditPasswordDialog(entry,passwordList);});

        deleteButton.setOnAction(event -> {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Delete Password");
            confirmation.setHeaderText("Delete this password?");
            confirmation.setContentText("Are you sure you want to delete this password?\n" + "This action cannot be undone.");
            applyDialogTheme(confirmation);
            confirmation.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK){
                    vaultService.deleteEntry(entry);
                    passwordList.getItems().remove(entry);
                    root.setCenter(passwordCenter);
                }
            });
        });

        HBox actions = new HBox(10,editButton,deleteButton);

        actions.setAlignment(Pos.CENTER_RIGHT);

        actions.setPadding(new Insets(20, 0, 0, 0));

        detailsRoot.setBottom(actions);

        root.setCenter(detailsRoot);

        ThemeManager.applyThemeToRoot(root,ThemeManager.getCurrentTheme());
    }


    public Parent createContent(){

        root = new BorderPane();

        root.setPadding(new Insets(20));

        passwordList = new ListView<>();

        passwordList.getItems().setAll(vaultService.getEntries());

        TopBar topBar = new TopBar();

        topBar.getAddButton().setOnAction(event -> showAddPasswordDialog(passwordList));

        topBar.getSettingsButton().setOnAction(event -> showSettings());

        root.setTop(topBar);

        passwordList.setCellFactory(list -> {

            return new ListCell<PasswordEntry>() {
                @Override
                protected void updateItem(
                        PasswordEntry entry,
                        boolean empty
                ){super.updateItem(entry,empty);

                    if (empty || entry == null){
                        setText(null);
                        setGraphic(null);
                        return;}

                    Label website =new Label(entry.getWebsite());

                    website.setStyle("-fx-font-size: 13px;"+ "-fx-text-fill: #888888;");

                    Label username =new Label(entry.getUsername());

                    username.setStyle("-fx-font-size: 13px;"+ "-fx-text-fill: #888888;");

                    VBox info = new VBox(4,website,username);

                    Label arrow = new Label(">");

                    arrow.setStyle("-fx-font-size: 20px;"+ "-fx-text-fill: #888888;");

                    Region spacer = new Region();

                    HBox.setHgrow(spacer,Priority.ALWAYS);

                    HBox row = new HBox(15,info,spacer,arrow);

                    row.setPadding(new Insets(12,15,12,15));

                    row.setAlignment(Pos.CENTER_LEFT);

                    setGraphic(row);
                    setText(null);

                    setOnMouseClicked(event -> {if (event.getClickCount() == 1){showPasswordDetails(entry,passwordList);}});
            }
        };});


        passwordCenter = new VBox();

        VBox.setVgrow(passwordList,Priority.ALWAYS);

        passwordCenter.getChildren().add(passwordList);

        root.setCenter(passwordCenter);

        ThemeManager.applyThemeToRoot(root,ThemeManager.getCurrentTheme());

        return root;
    }


    private void showSettings(){

        SettingsView settingsView = new SettingsView(vaultService,this::showDashboard,onLock);

        root.setTop(null);
        root.setCenter(settingsView.createContent());
    }


    private void showDashboard(){

        passwordList.getItems().setAll(vaultService.getEntries());

        TopBar topBar = new TopBar();

        topBar.getAddButton().setOnAction(event ->showAddPasswordDialog(passwordList));

        topBar.getSettingsButton().setOnAction(event ->showSettings());

        root.setTop(topBar);
        root.setCenter(passwordCenter);

        ThemeManager.applyThemeToRoot(root,ThemeManager.getCurrentTheme());
    }
}