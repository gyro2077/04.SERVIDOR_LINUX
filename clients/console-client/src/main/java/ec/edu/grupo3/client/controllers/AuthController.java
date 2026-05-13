package ec.edu.grupo3.client.controllers;

import ec.edu.grupo3.client.models.UserSession;
import ec.edu.grupo3.client.views.AuthView;

public class AuthController {
    
    private static final String VALID_USER = "MONSTER";
    private static final String VALID_PASS = "MONSTER9";

    private final AuthView authView = new AuthView();

    public UserSession authenticate() {
        while (true) {
            authView.showBanner();
            String user = authView.getUsername();
            String pass = authView.getPassword();

            if (VALID_USER.equals(user.trim()) && VALID_PASS.equals(pass)) {
                authView.showSuccess(user.trim());
                return new UserSession(user.trim(), true);
            } else {
                authView.showError("Credenciales incorrectas. Verifique su usuario y contraseña.");
            }
        }
    }
}