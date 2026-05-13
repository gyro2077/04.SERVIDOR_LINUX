package ec.edu.grupo3.client.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView {

    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label errorLabel;
    private Runnable onLoginAction;

    public void setOnLoginAction(Runnable action) {
        this.onLoginAction = action;
    }

    public VBox getView() {
        VBox mainContainer = new VBox();
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: #0f172a;");
        mainContainer.setPadding(new Insets(40));

        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setStyle(
            "-fx-background-color: #1e293b; " +
            "-fx-background-radius: 20; " +
            "-fx-border-radius: 20; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0.5, 0, 10);"
        );
        card.setPrefWidth(400);

        Label titleLabel = new Label("Acceso Seguro");
        titleLabel.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: #f8fafc;");

        Label subtitleLabel = new Label("Ingrese sus credenciales");
        subtitleLabel.setFont(Font.font("System", 12));
        subtitleLabel.setStyle("-fx-text-fill: #94a3b8;");

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #fca5a5; -fx-font-size: 12;");
        errorLabel.setVisible(false);

        usernameField = new TextField();
        usernameField.setPromptText("Usuario");
        usernameField.setPrefHeight(45);
        usernameField.setStyle(
            "-fx-background-color: #334155; " +
            "-fx-text-fill: white; " +
            "-fx-prompt-text-fill: #94a3b8; " +
            "-fx-background-radius: 10; " +
            "-fx-border-radius: 10; " +
            "-fx-border-color: #475569; " +
            "-fx-border-width: 1; " +
            "-fx-padding: 0 15;"
        );

        passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");
        passwordField.setPrefHeight(45);
        passwordField.setStyle(
            "-fx-background-color: #334155; " +
            "-fx-text-fill: white; " +
            "-fx-prompt-text-fill: #94a3b8; " +
            "-fx-background-radius: 10; " +
            "-fx-border-radius: 10; " +
            "-fx-border-color: #475569; " +
            "-fx-border-width: 1; " +
            "-fx-padding: 0 15;"
        );

        loginButton = new Button("INGRESAR AL SISTEMA");
        loginButton.setPrefHeight(45);
        loginButton.setPrefWidth(280);
        loginButton.setStyle(
            "-fx-background-color: #6366f1; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 14; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand;"
        );

        if (onLoginAction != null) {
            loginButton.setOnAction(e -> onLoginAction.run());
        }

        card.getChildren().addAll(
            titleLabel,
            subtitleLabel,
            new Label(),
            usernameField,
            passwordField,
            errorLabel,
            loginButton
        );

        mainContainer.getChildren().add(card);
        return mainContainer;
    }

    public TextField getUsernameField() {
        return usernameField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public void hideError() {
        errorLabel.setVisible(false);
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public void clearFields() {
        usernameField.clear();
        passwordField.clear();
        hideError();
    }
}