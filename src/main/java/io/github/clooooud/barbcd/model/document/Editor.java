package io.github.clooooud.barbcd.model.document;

import java.util.List;

public class Editor implements Saveable {

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

    public Editor(int id, String nom) {
        this.id = id;
        this.nom = nom;
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

    @Override
    public List<Object> getHeaders() {
        return List.of("id", "nom");
    }
}
