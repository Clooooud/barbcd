package io.github.clooooud.barbcd.data.auth;

import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.SaveableType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class User implements Saveable {

    private int id;
    private String login;
    private String passwordHash;
    private String mainPassword;

    public User(int id, String login, String passwordHash, String mainPassword) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.mainPassword = mainPassword;
    }

    public boolean isAdmin() {
        return login.equalsIgnoreCase("admin");
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getMainPassword() {
        return mainPassword;
    }

    public void setMainPassword(String mainPassword) {
        this.mainPassword = mainPassword;
    }

    @Override
    public SaveableType getSaveableType() {
        return SaveableType.USER;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public List<Object> getValues() {
        return List.of(this.id, this.login, this.passwordHash, this.mainPassword);
    }
}
