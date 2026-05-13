package ec.edu.grupo3.client.model;

public class AuthModel {

    private static final String VALID_USER = "MONSTER";
    private static final String VALID_PASS = "MONSTER9";

    public boolean authenticate(String username, String password) {
        return VALID_USER.equals(username.trim()) && VALID_PASS.equals(password);
    }

    public String getValidUser() {
        return VALID_USER;
    }
}