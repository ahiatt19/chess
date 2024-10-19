package model;

public record UserData(String username, String password, String email) {
    public boolean isUserNameEqual(String otherUsername) {
        return username.equals(otherUsername);
    }
}