package io.github.clooooud.barbcd;

import io.github.clooooud.barbcd.data.PublicCredentials;
import io.github.clooooud.barbcd.gui.StageWrapper;
import io.github.clooooud.barbcd.gui.scenes.MainScene;
import io.github.clooooud.barbcd.gui.scenes.StartScene;
import io.github.clooooud.barbcd.model.Library;
import io.github.clooooud.barbcd.model.document.Editor;
import io.github.clooooud.barbcd.model.document.Oeuvre;
import io.github.clooooud.barbcd.model.document.Categorie;
import javafx.application.Application;
import javafx.stage.Stage;

public class BarBCD extends Application {

    private StageWrapper stageWrapper;
    private PublicCredentials credentials;
    private final Library library = new Library("");

    @Override
    public void start(Stage stage) {
        library.addOeuvre(new Oeuvre(1, "Titre1", "Auteur", "55646", new Editor(1, "Editeur"), new Categorie(2, "Type"), 1, 1984));
        library.addOeuvre(new Oeuvre(2, "Titre2", "Auteur", "55646", new Editor(1, "Editeur"), new Categorie(2, "Type"), 0, 1984));
        library.addOeuvre(new Oeuvre(3, "Titre3", "Auteur", "55646", new Editor(1, "Editeur"), new Categorie(2, "Type"), 1, 1984));
        library.addOeuvre(new Oeuvre(4, "Titre4", "Auteur", "55646", new Editor(1, "Editeur"), new Categorie(2, "Type"), 0, 1984));
        library.addOeuvre(new Oeuvre(5, "Titre5", "Auteur", "55646", new Editor(1, "Editeur"), new Categorie(2, "Type"), 1, 1984));
        library.addOeuvre(new Oeuvre(6, "Titre6", "Auteur", "55646", new Editor(1, "Editeur"), new Categorie(2, "Type"), 0, 1984));
        library.addOeuvre(new Oeuvre(7, "Titre7", "Auteur", "55646", new Editor(1, "Editeur"), new Categorie(2, "Type"), 1, 1984));
        library.addOeuvre(new Oeuvre(8, "Titre8", "Auteur", "55646", new Editor(1, "Editeur"), new Categorie(2, "Type"), 0, 1984));


        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setTitle("BarBCD");
        this.credentials = new PublicCredentials();
        this.stageWrapper = new StageWrapper(stage);
        this.stageWrapper.setContent(this.credentials.isFileExisted() ? new MainScene(this) : new StartScene(this));
        stage.show();
    }

    public StageWrapper getStageWrapper() {
        return stageWrapper;
    }

    public Library getLibrary() {
        return library;
    }

    public static void main(String[] args) {
        launch();
    }
}