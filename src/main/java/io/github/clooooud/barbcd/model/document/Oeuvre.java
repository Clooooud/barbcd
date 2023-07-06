package io.github.clooooud.barbcd.model.document;

import java.util.List;

public class Oeuvre implements ViewableDocument, Saveable {

    private int id;
    private String title, author, isbn;
    private Editor editor;
    private OeuvreType oeuvreType;
    private int quantity, year;

    private boolean needUpdate = false;

    @Override
    public void markAsUpdated() {
        needUpdate = true;
    }

    @Override
    public boolean needsSave() {
        return needUpdate;
    }

    public Oeuvre(int id, String title, String author, String isbn, Editor editor, OeuvreType oeuvreType, int quantity, int year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.editor = editor;
        this.oeuvreType = oeuvreType;
        this.quantity = quantity;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getDate() {
        return String.valueOf(year);
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
        return List.of(id, title, author, isbn, editor.getId(), oeuvreType.getId(), quantity, year);
    }

    @Override
    public List<Object> getHeaders() {
        return List.of("id", "title", "author", "isbn", "editor id", "oeuvre type id", "quantity", "year");
    }
}
