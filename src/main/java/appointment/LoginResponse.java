package appointment;

class LoginResponse {
    private Users user;
    private boolean isDefaultPassword;

    public LoginResponse(Users user, boolean isDefaultPassword) {
        this.user = user;
        this.isDefaultPassword = isDefaultPassword;
    }

    // Getters and Setters
    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public boolean isDefaultPassword() {
        return isDefaultPassword;
    }

    public void setDefaultPassword(boolean defaultPassword) {
        isDefaultPassword = defaultPassword;
    }
}
