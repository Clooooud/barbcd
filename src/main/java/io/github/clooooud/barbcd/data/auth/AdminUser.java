package io.github.clooooud.barbcd.data.auth;

public class AdminUser extends User {

    public AdminUser(String passwordHash) {
        super(1, "admin", passwordHash);
    }
}
