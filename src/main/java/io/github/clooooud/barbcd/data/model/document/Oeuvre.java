package io.github.clooooud.barbcd.data.model.document;

import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.Saveable;

import java.util.List;

public class Oeuvre implements ViewableDocument, Saveable {

    private final int id;
    private final String title;
    private final String author;
    private final String isbn;
    private final Editor editor;
    private Category category;
    private final int quantity;
    private final int year;

    public Oeuvre(int id, String title, String author, String isbn, Editor editor, Category category, int quantity, int year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.editor = editor;
        this.category = category;
        this.quantity = quantity;
        this.year = year;
    }

    @Override
    public SaveableType getSaveableType() {
        return SaveableType.OEUVRE;
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
    public Category getCategory() {
        return this.category;
    }

    @Override
    public int getQuantity() {
        return this.quantity;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public Editor getEditor() {
        return this.editor;
    }

    @Override
    public List<Object> getValues() {
        return List.of(id, title, author, isbn, editor.getId(), category == null ? -1 : category.getId(), quantity, year);
    }
}
