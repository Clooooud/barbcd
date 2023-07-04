package io.github.clooooud.barbcd.model.document;

import java.util.List;

public class MagazineSerie implements Saveable {

    private int id;
    private String title, isbn;
    private Editor editor;

    private boolean needUpdate = false;

    @Override
    public void markAsUpdated() {
        needUpdate = true;
    }

    @Override
    public boolean needsSave() {
        return needUpdate;
    }

    public MagazineSerie(int id, String title, String isbn, Editor editor) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.editor = editor;
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

    @Override
    public List<Object> getHeaders() {
        return List.of("id", "title", "isbn", "editor id");
    }
}
