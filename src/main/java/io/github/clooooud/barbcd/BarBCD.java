package io.github.clooooud.barbcd;

import io.github.clooooud.barbcd.gui.RootScene;
import io.github.clooooud.barbcd.gui.content.MainPage;
import io.github.clooooud.barbcd.model.Library;
import io.github.clooooud.barbcd.model.document.Editor;
import io.github.clooooud.barbcd.model.document.Oeuvre;
import io.github.clooooud.barbcd.model.document.OeuvreType;
import javafx.application.Application;
import javafx.stage.Stage;

public class BarBCD extends Application {

    private final Library library = new Library("");
    private final String adminPassword = "";

    @Override
    public void start(Stage stage) {
        library.addOeuvre(new Oeuvre(1, "Titre", "Auteur", "55646", new Editor(1, "Editeur"), new OeuvreType(2, "Type"), 1, 1984));
        library.addOeuvre(new Oeuvre(2, "Titre2", "Auteur", "55646", new Editor(1, "Editeur"), new OeuvreType(2, "Type"), 0, 1984));
        library.addOeuvre(new Oeuvre(3, "Titre3", "Auteur", "55646", new Editor(1, "Editeur"), new OeuvreType(2, "Type"), 1, 1984));
        library.addOeuvre(new Oeuvre(4, "Titre4", "Auteur", "55646", new Editor(1, "Editeur"), new OeuvreType(2, "Type"), 0, 1984));
        library.addOeuvre(new Oeuvre(5, "Titre5", "Auteur", "55646", new Editor(1, "Editeur"), new OeuvreType(2, "Type"), 1, 1984));
        library.addOeuvre(new Oeuvre(6, "Titre6", "Auteur", "55646", new Editor(1, "Editeur"), new OeuvreType(2, "Type"), 0, 1984));
        library.addOeuvre(new Oeuvre(7, "Titre7", "Auteur", "55646", new Editor(1, "Editeur"), new OeuvreType(2, "Type"), 1, 1984));
        library.addOeuvre(new Oeuvre(8, "Titre8", "Auteur", "55646", new Editor(1, "Editeur"), new OeuvreType(2, "Type"), 0, 1984));


        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setTitle("BarBCD");
        RootScene rootScene = new RootScene(this);
        rootScene.setAndUpdateContent(new MainPage(library));
        stage.setScene(rootScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public boolean isAdmin() {
        return !adminPassword.isEmpty();
    }
}