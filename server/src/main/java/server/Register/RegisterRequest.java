package server.Register;

public class RegisterRequest {
    String username;
    String password;
    String email;

    public String getUsername() { return this.username; }

    public String getPassword() {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }
}
