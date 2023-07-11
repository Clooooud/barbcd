package io.github.clooooud.barbcd.data.model.document;

import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.Saveable;

import java.util.List;
import java.util.Objects;

public class Categorie implements Saveable {

    public static Categorie MAGAZINE = new Categorie(1, "Périodique");

    private final int id;
    private final String nom;

    public Categorie(int id, String nom) {
        if (id == 1) {
            MAGAZINE = this;
        }

        this.id = id;
        this.nom = nom;
    }

    @Override
    public SaveableType getSaveableType() {
        return SaveableType.CATEGORIE;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categorie that = (Categorie) o;
        return id == that.id && Objects.equals(nom, that.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
