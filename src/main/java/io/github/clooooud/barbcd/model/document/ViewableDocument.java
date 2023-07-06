package io.github.clooooud.barbcd.model.document;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ViewableDocument {

    default String getSearchString() {
        return String.join(" ", getAuthor(), getTitle(), getISBN(), getType().getNom(), getEditor().getNom(), isAvailable() ? "Disponible" : "Indisponible");
    }

    String getISBN();

    String getTitle();

    String getAuthor();

    OeuvreType getType();

    Editor getEditor();

    String getDate();

    int getQuantity();

    default boolean isAvailable() {
        return getQuantity() > 0;
    }
}
