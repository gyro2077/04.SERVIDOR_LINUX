package ec.edu.grupo3.client;

import ec.edu.grupo3.client.controller.ConversionController;
import ec.edu.grupo3.client.view.LoginView;
import ec.edu.grupo3.client.view.MainView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DesktopClient extends Application {

    private Stage primaryStage;
    private LoginView loginView;
    private MainView mainView;
    private String authenticatedUser;
    private ConversionController conversionController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("SOAP Hub - Cliente de Escritorio");

        loginView = new LoginView();
        
        loginView.setOnLoginAction(() -> handleLogin());

        Scene scene = new Scene(loginView.getView(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleLogin() {
        String username = loginView.getUsername();
        String password = loginView.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            loginView.showError("Por favor complete todos los campos");
            return;
        }

        if (username.trim().equals("MONSTER") && password.equals("MONSTER9")) {
            authenticatedUser = username.trim();
            showMainScene();
        } else {
            loginView.showError("Usuario o contraseña incorrectos");
            loginView.clearFields();
        }
    }

    private void showMainScene() {
        mainView = new MainView();
        mainView.getView(authenticatedUser); // Initialize view components first
        conversionController = new ConversionController(mainView);
        
        mainView.getLogoutButton().setOnAction(e -> {
            loginView = new LoginView();
            loginView.setOnLoginAction(() -> handleLogin());
            primaryStage.setScene(new Scene(loginView.getView(), 800, 600));
        });

        Scene scene = new Scene(mainView.getView(authenticatedUser), 900, 700);
        primaryStage.setScene(scene);
    }
}