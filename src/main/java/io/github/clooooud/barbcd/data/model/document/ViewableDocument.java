package io.github.clooooud.barbcd.data.model.document;

import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.model.Library;

public interface ViewableDocument {

    default String getSearchString(Library library) {
        return String.join(" ", getAuthor(), getTitle(), getISBN(), getCategorie().getNom(), getEditor().getName(), isAvailable(library) ? "Disponible" : "Indisponible");
    }

    String getISBN();

    String getTitle();

    String getAuthor();

    Categorie getCategorie();

    Editor getEditor();

    String getDate();

    int getQuantity();

    default boolean isAvailable(Library library) {
        return getQuantity() - getNumberOfActiveBorrowings(library) > 0;
    }

    private int getNumberOfActiveBorrowings(Library library) {
        return (int) library.getDocuments(SaveableType.BORROWING).stream()
                .map(saveable -> (Borrowing) saveable)
                .filter(borrowing -> !borrowing.isFinished())
                .filter(borrowing -> borrowing.getBorrowedDocument().equals(this))
                .count();
    }
}
