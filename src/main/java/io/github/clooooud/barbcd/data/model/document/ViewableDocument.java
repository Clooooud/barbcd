package io.github.clooooud.barbcd.data.model.document;

import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.model.Library;

public interface ViewableDocument {

    default String getSearchString(Library library) {
        Category category = getCategory();
        String categoryName = category == null ? "" : category.getName();
        return String.join(" ", getAuthor(), getTitle(), getISBN(), categoryName, getEditor().getName(), isAvailable(library) ? "Disponible" : "Indisponible");
    }

    String getISBN();

    String getTitle();

    String getAuthor();

    Category getCategory();

    Editor getEditor();

    String getDate();

    int getQuantity();

    default boolean isAvailable(Library library) {
        return getQuantity() - getNumberOfBorrowings(library) > 0;
    }

    default int getNumberOfBorrowings(Library library) {
        return (int) library.getDocuments(SaveableType.BORROWING).stream()
                .map(saveable -> (Borrowing) saveable)
                .filter(borrowing -> borrowing.getBorrowedDocument().equals(this))
                .count();
    }
}
