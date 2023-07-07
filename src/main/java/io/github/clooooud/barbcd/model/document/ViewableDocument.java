package io.github.clooooud.barbcd.model.document;

public interface ViewableDocument {

    default String getSearchString() {
        return String.join(" ", getAuthor(), getTitle(), getISBN(), getCategorie().getNom(), getEditor().getNom(), isAvailable() ? "Disponible" : "Indisponible");
    }

    String getISBN();

    String getTitle();

    String getAuthor();

    Categorie getCategorie();

    Editor getEditor();

    String getDate();

    int getQuantity();

    default boolean isAvailable() {
        return getQuantity() > 0;
    }
}
