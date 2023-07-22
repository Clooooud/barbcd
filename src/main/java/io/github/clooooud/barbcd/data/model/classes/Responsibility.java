package io.github.clooooud.barbcd.data.model.classes;

import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.auth.User;

import java.util.List;
import java.util.Objects;

public class Responsibility implements Saveable {

    private final int id;
    private final User user;
    private final Class clazz;

    public Responsibility(int id, User user, Class clazz) {
        this.id = id;
        this.user = user;
        this.clazz = clazz;
    }

    public User getUser() {
        return user;
    }

    public Class getOwnedClass() {
        return clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Responsibility that = (Responsibility) o;
        return Objects.equals(user, that.user) && Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, clazz);
    }

    @Override
    public SaveableType getSaveableType() {
        return SaveableType.RESPONSIBILITY;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public List<Object> getValues() {
        return List.of(this.id, user.getId(), clazz.getId());
    }
}
