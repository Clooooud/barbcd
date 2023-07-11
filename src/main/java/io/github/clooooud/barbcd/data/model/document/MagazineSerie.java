package io.github.clooooud.barbcd.data.model.document;

import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.Saveable;

import java.util.List;

public class MagazineSerie implements Saveable {

    private final int id;
    private final String title;
    private final String isbn;
    private final Editor editor;

    public MagazineSerie(int id, String title, String isbn, Editor editor) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.editor = editor;
    }

    @Override
    public SaveableType getSaveableType() {
        return SaveableType.MAGAZINE_SERIE;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getISBN() {
        return isbn;
    }

    public Editor getEditor() {
        return editor;
    }

    @Override
    public List<Object> getValues() {
        return List.of(id, title, isbn, editor.getId());
    }
}
