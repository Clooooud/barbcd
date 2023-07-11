package io.github.clooooud.barbcd.data;

import java.util.List;

public enum SaveableType {
    OEUVRE("Oeuvres", List.of("id", "title", "author", "isbn", "editor id", "categorie id", "quantity", "year")),
    CATEGORIE("Categories", List.of("id", "nom")),
    MAGAZINE("Magazines", List.of("id", "title", "magazine number", "quantity", "year", "month", "magazine serie id")),
    MAGAZINE_SERIE("MagazineSeries", List.of("id", "title", "isbn", "editor id")),
    EDITOR("Editors", List.of("id", "nom")),
    USER("Users", List.of("id", "login", "password hash")),
    BORROWING("Borrowings", List.of("id", "user id", "is magazine", "document id", "is finished"));

    public static List<SaveableType> getOrderedTypes() {
        return List.of(CATEGORIE, EDITOR, MAGAZINE_SERIE, MAGAZINE, OEUVRE, USER, BORROWING);
    }

    private final String sheetName;
    private final List<Object> headers;

    SaveableType(String sheetName, List<Object> headers) {
        this.sheetName = sheetName;
        this.headers = headers;
    }

    public List<Object> getHeaders() {
        return headers;
    }

    public String getSheetName() {
        return sheetName;
    }
}
