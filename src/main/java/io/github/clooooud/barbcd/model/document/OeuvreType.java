package io.github.clooooud.barbcd.model.document;

import java.util.List;
import java.util.Objects;

public class OeuvreType implements Saveable {

    public static final OeuvreType MAGAZINE = new OeuvreType(1, "Périodique");

    private final int id;
    private final String nom;

    private boolean needUpdate = false;

    @Override
    public void markAsUpdated() {
        needUpdate = true;
    }

    @Override
    public boolean needsSave() {
        return needUpdate;
    }

    public OeuvreType(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public int getId() {
        return id;
    }

    @Override
    public List<Object> getValues() {
        return List.of(id, nom);
    }

    @Override
    public List<Object> getHeaders() {
        return List.of("id", "nom");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OeuvreType that = (OeuvreType) o;
        return id == that.id && Objects.equals(nom, that.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
