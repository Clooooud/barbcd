package io.github.clooooud.barbcd.model.document;

public interface ViewableDocument {

    String getISBN();

    String getTitle();

    String getAuthor();

    OeuvreType getType();

    Editor getEditor();

    int getQuantity();

    default boolean isAvailable() {
        return getQuantity() > 0;
    }
}
