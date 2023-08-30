package io.github.clooooud.barbcd.data.model.document;

import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.model.Library;

import java.util.List;
import java.util.stream.Stream;

public class Editor implements Saveable {

    private final int id;
    private String name;

    public Editor(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public List<ViewableDocument> getEditedDocuments(Library library) {
        return Stream.concat(
                library.getDocuments(SaveableType.OEUVRE).stream(),
                library.getDocuments(SaveableType.MAGAZINE).stream()
        ).map(document -> (ViewableDocument) document)
                .filter(document -> document.getEditor().equals(this))
                .toList();
    }

    @Override
    public String toString() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public SaveableType getSaveableType() {
        return SaveableType.EDITOR;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public List<Object> getValues() {
        return List.of(id, name);
    }
}
