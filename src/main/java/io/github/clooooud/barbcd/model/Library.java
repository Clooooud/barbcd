package io.github.clooooud.barbcd.model;

import io.github.clooooud.barbcd.model.document.*;

import java.util.HashSet;
import java.util.Set;

public class Library {

    private final Set<Oeuvre> oeuvreList;
    private final Set<Magazine> magazineList;
    private final Set<Editor> editorList;
    private final Set<Categorie> categorieList;
    private final Set<MagazineSerie> magazineSerieList;

    private final String name;
    private String adminPassword;

    public Library(String name) {
        this.name = name;
        oeuvreList = new HashSet<>();
        magazineList = new HashSet<>();
        editorList = new HashSet<>();
        categorieList = new HashSet<>();
        magazineSerieList = new HashSet<>();
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public void clearAdminPassword() {
        setAdminPassword("");
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public boolean isAdmin() {
        return !adminPassword.isEmpty();
    }

    public Set<Oeuvre> getOeuvreList() {
        return oeuvreList;
    }

    public Set<Magazine> getMagazineList() {
        return magazineList;
    }

    public Set<Editor> getEditorList() {
        return editorList;
    }

    public Set<Categorie> getCategorieList() {
        return categorieList;
    }

    public Set<MagazineSerie> getMagazineSerieList() {
        return magazineSerieList;
    }

    public Oeuvre getOeuvre(int id) {
        return getOeuvreList()
                .stream()
                .filter(oeuvre -> oeuvre.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Magazine getMagazine(int id) {
        return getMagazineList()
                .stream()
                .filter(magazine -> magazine.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Editor getEditor(int id) {
        return getEditorList()
                .stream()
                .filter(editor -> editor.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Categorie getCategorie(int id) {
        return getCategorieList()
                .stream()
                .filter(categorie -> categorie.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public MagazineSerie getMagazineSerie(int id) {
        return getMagazineSerieList()
                .stream()
                .filter(magazineSerie -> magazineSerie.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public int getNextOeuvreId() {
        return oeuvreList.stream().mapToInt(Oeuvre::getId).max().orElse(0) + 1;
    }

    public int getNextMagazineId() {
        return magazineList.stream().mapToInt(Magazine::getId).max().orElse(0) + 1;
    }

    public int getNextEditorId() {
        return editorList.stream().mapToInt(Editor::getId).max().orElse(0) + 1;
    }

    public int getNextCategorieId() {
        return categorieList.stream().mapToInt(Categorie::getId).max().orElse(0) + 1;
    }

    public int getNextMagazineSerieId() {
        return magazineSerieList.stream().mapToInt(MagazineSerie::getId).max().orElse(0) + 1;
    }

    public void addOeuvre(Oeuvre oeuvre) {
        oeuvreList.add(oeuvre);
    }

    public void addMagazine(Magazine magazine) {
        magazineList.add(magazine);
    }

    public void addEditor(Editor editor) {
        editorList.add(editor);
    }

    public void addCategorie(Categorie categorie) {
        categorieList.add(categorie);
    }

    public void addMagazineSerie(MagazineSerie magazineSerie) {
        magazineSerieList.add(magazineSerie);
    }

    @Override
    public String toString() {
        return "Library{" +
                "oeuvreList=" + oeuvreList +
                ", magazineList=" + magazineList +
                ", editorList=" + editorList +
                ", categorieList=" + categorieList +
                ", magazineSerieList=" + magazineSerieList +
                '}';
    }
}
