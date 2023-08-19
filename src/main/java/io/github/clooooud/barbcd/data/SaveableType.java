package io.github.clooooud.barbcd.data;

import java.util.List;

public enum SaveableType {
    OEUVRE("Oeuvres", List.of("id", "title", "author", "isbn", "editor id", "categorie id", "quantity", "year")),
    CATEGORIE("Categories", List.of("id", "nom", "parent category id")),
    MAGAZINE("Magazines", List.of("id", "title", "magazine number", "quantity", "year", "month", "magazine serie id")),
    MAGAZINE_SERIE("MagazineSeries", List.of("id", "title", "isbn", "editor id")),
    EDITOR("Editors", List.of("id", "nom")),
    USER("Users", List.of("id", "login", "password hash", "main password")),
    BORROWING("Borrowings", List.of("id", "student id", "is magazine", "document id")),
    STUDENT("Students", List.of("id", "first name", "last name", "class id")),
    CLASS("Classes", List.of("id", "class name")),
    SETTINGS("Settings", List.of("id", "library name")),
    RESPONSIBILITY("Responsibilities", List.of("id", "user id", "class id"));

    public static List<SaveableType> getOrderedTypes() {
        return List.of(SETTINGS, CATEGORIE, EDITOR, MAGAZINE_SERIE, MAGAZINE, OEUVRE, USER, BORROWING, CLASS, STUDENT, RESPONSIBILITY);
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
