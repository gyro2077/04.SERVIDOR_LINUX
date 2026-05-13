package ec.edu.grupo3.client.controller;

import ec.edu.grupo3.client.model.AuthModel;
import ec.edu.grupo3.client.view.LoginView;
import javafx.application.Platform;

public class AuthController {

    private final AuthModel authModel;
    private final LoginView loginView;
    private final Runnable onLoginSuccess;

    public AuthController(LoginView loginView, Runnable onLoginSuccess) {
        this.authModel = new AuthModel();
        this.loginView = loginView;
        this.onLoginSuccess = onLoginSuccess;

        setupEventHandlers();
    }

    private void setupEventHandlers() {
        loginView.getLoginButton().setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String username = loginView.getUsername();
        String password = loginView.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            loginView.showError("Por favor complete todos los campos");
            return;
        }

        if (authModel.authenticate(username, password)) {
            onLoginSuccess.run();
        } else {
            loginView.showError("Usuario o contraseña incorrectos");
            loginView.clearFields();
        }
    }
}