package io.github.clooooud.barbcd.data.auth;

import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.model.Library;
import io.github.clooooud.barbcd.data.model.classes.Class;
import io.github.clooooud.barbcd.data.model.classes.Responsibility;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class User implements Saveable {

    private final int id;
    private final String login;
    private String password;
    private String mainPassword;

    public User(int id, String login, String password, String mainPassword) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.mainPassword = mainPassword;
    }

    public Set<Responsibility> getResponsibilities(Library library) {
        return library.getDocuments(SaveableType.RESPONSIBILITY).stream()
                .map(saveable -> (Responsibility) saveable)
                .filter(responsibility -> responsibility.getUser().equals(this))
                .collect(Collectors.toSet());
    }

    public Set<Class> getOwnedClasses(Library library) {
        return getResponsibilities(library).stream()
                .map(Responsibility::getOwnedClass)
                .collect(Collectors.toSet());
    }

    public boolean isAdmin() {
        return login.equalsIgnoreCase("admin");
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMainPassword() {
        return mainPassword;
    }

    public void setMainPassword(String mainPassword) {
        this.mainPassword = mainPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(login, user.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
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
        return List.of(this.id, this.login, this.password, this.mainPassword);
    }
}
