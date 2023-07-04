package io.github.clooooud.barbcd.model.document;

import java.util.List;

public class Oeuvre implements ViewableDocument, Saveable {

    private int id;
    private String title, author, isbn;
    private Editor editor;
    private OeuvreType oeuvreType;
    private int quantity;

    private boolean needUpdate = false;

    @Override
    public void markAsUpdated() {
        needUpdate = true;
    }

    @Override
    public boolean needsSave() {
        return needUpdate;
    }

    public Oeuvre(int id, String title, String author, String isbn, Editor editor, OeuvreType oeuvreType, int quantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.editor = editor;
        this.oeuvreType = oeuvreType;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getISBN() {
        return this.isbn;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getAuthor() {
        return this.author;
    }

    @Override
    public OeuvreType getType() {
        return this.oeuvreType;
    }

    @Override
    public int getQuantity() {
        return this.quantity;
    }

    @Override
    public Editor getEditor() {
        return this.editor;
    }

    @Override
    public List<Object> getValues() {
        return List.of(id, title, isbn, author, editor.getId(), oeuvreType.getId(), quantity);
    }

    @Override
    public List<Object> getHeaders() {
        return List.of("id", "title", "isbn", "author", "editor id", "oeuvre type id", "quantity");
    }
}
