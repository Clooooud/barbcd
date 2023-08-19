package io.github.clooooud.barbcd.data.model.document;

import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.Saveable;

import java.util.List;

public class Magazine implements ViewableDocument, Saveable {

    private final int id;
    private final String title;
    private final int magazineNumber;
    private final int quantity;
    private final int year;
    private final int month;
    private final MagazineSerie magazineSerie;

    public Magazine(int id, String title, int magazineNumber, int quantity, int year, int month, MagazineSerie magazineSerie) {
        this.id = id;
        this.title = title;
        this.magazineNumber = magazineNumber;
        this.quantity = quantity;
        this.magazineSerie = magazineSerie;
        this.year = year;
        this.month = month;
    }

    @Override
    public SaveableType getSaveableType() {
        return SaveableType.MAGAZINE;
    }

    @Override
    public String getDate() {
        return month + "/" + year;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getISBN() {
        return magazineSerie.getISBN();
    }

    @Override
    public String getTitle() {
        return magazineSerie.getTitle() + " - " + title + " (" + this.magazineNumber + ")";
    }

    @Override
    public String getAuthor() {
        return getEditor().getName();
    }

    @Override
    public Categorie getCategorie() {
        return Categorie.MAGAZINE;
    }

    @Override
    public int getQuantity() {
        return this.quantity;
    }

    @Override
    public Editor getEditor() {
        return this.magazineSerie.getEditor();
    }

    @Override
    public List<Object> getValues() {
        return List.of(this.id, this.title, this.magazineNumber, this.quantity, this.year, this.month, this.magazineSerie.getId());
    }
}
