package io.github.clooooud.barbcd.data.model.document;

import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.Saveable;

import java.util.List;

public class Editor implements Saveable {

    private final int id;
    private final String nom;

    public Editor(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    @Override
    public SaveableType getSaveableType() {
        return SaveableType.EDITOR;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    @Override
    public List<Object> getValues() {
        return List.of(id, nom);
    }
}
