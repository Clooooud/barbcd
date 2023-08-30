package io.github.clooooud.barbcd.data.model.document;

import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.model.Library;

import java.util.List;
import java.util.Objects;

public class Category implements Saveable {

    public static Category MAGAZINE = new Category(1, "Périodique");

    private final int id;
    private final String name;
    private final int parentId;

    public Category(int id, String name) {
        this(id, name, -1);
    }

    public Category(int id, String name, int parentId) {
        if (id == 1) {
            MAGAZINE = this;
        }

        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    @Override
    public SaveableType getSaveableType() {
        return SaveableType.CATEGORY;
    }

    public String getName() {
        return name;
    }

    public Category getParent(Library library) {
        if (this.parentId < 0) {
            return null;
        }

        return (Category) library.getDocumentById(SaveableType.CATEGORY, this.parentId);
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public List<Object> getValues() {
        return List.of(id, name, parentId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category that = (Category) o;
        return id == that.id && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
