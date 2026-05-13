package ec.edu.grupo3.client.models;

public class UserSession {
    private String username;
    private boolean authenticated;

    public UserSession(String username, boolean authenticated) {
        this.username = username;
        this.authenticated = authenticated;
    }

    public String getUsername() { return username; }
    public boolean isAuthenticated() { return authenticated; }
}