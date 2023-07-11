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
    private final Categorie categorie;
    private final int quantity;
    private final int year;

    public Oeuvre(int id, String title, String author, String isbn, Editor editor, Categorie categorie, int quantity, int year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.editor = editor;
        this.categorie = categorie;
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
    public Categorie getCategorie() {
        return this.categorie;
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
        return List.of(id, title, author, isbn, editor.getId(), categorie.getId(), quantity, year);
    }
}
