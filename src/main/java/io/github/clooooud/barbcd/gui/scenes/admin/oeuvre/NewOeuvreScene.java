package io.github.clooooud.barbcd.gui.scenes.admin.oeuvre;

import com.google.api.services.books.model.Volume;
import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.GBookApi;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.model.document.Category;
import io.github.clooooud.barbcd.data.model.document.Editor;
import io.github.clooooud.barbcd.gui.element.*;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class NewOeuvreScene extends RootAdminScene {

    private FormBox isbnFormBox;
    private FormBox finalFormBox;
    private VBox vBox;

    public NewOeuvreScene(BarBCD app) {
        super(app);
        initForms();
    }

    @Override
    public void initAdminContent(VBox vBox) {
        this.vBox = vBox;

        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(this.isbnFormBox);
    }

    private void consumeIsbnForm() {
        GBookApi gBookApi = new GBookApi(this.getApp().getCredentials());

        FieldComponent isbnField = (FieldComponent) this.isbnFormBox.getComponent("isbn");
        String isbn = isbnField.getField().getText();

        Volume.VolumeInfo book;

        try {
            book = gBookApi.getBook(isbn);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (book == null) {
            GuiUtil.alertError("Aucun livre trouvé avec cet ISBN !");
            return;
        }

        String title = book.getTitle();
        String author = book.getAuthors().get(0);
        String year = book.getPublishedDate();
        String editorString = book.getPublisher();
        Editor editor = this.getLibrary().getDocuments(SaveableType.EDITOR).stream()
                .map(document -> (Editor) document)
                .filter(editor1 -> editor1.getName().equalsIgnoreCase(editorString))
                .findFirst().orElse(null);

        ((FieldComponent) this.finalFormBox.getComponent("title")).getField().setText(title);
        ((FieldComponent) this.finalFormBox.getComponent("author")).getField().setText(author);
        ((FieldComponent) this.finalFormBox.getComponent("isbn")).getField().setText(isbn);
        ((IntFieldComponent) this.finalFormBox.getComponent("year")).getField().setText(year);

        if (editor != null) {
            ((SearchFieldComponent<Editor>) this.finalFormBox.getComponent("editor")).getSearchField().getSelectionModel().select(editor);
        }

        this.vBox.getChildren().setAll(this.finalFormBox);
    }

    private void consumeFinalForm() {
        // Retrieve every variables then create the oeuvre
        String title = ((FieldComponent) this.finalFormBox.getComponent("title")).getField().getText();
        String author = ((FieldComponent) this.finalFormBox.getComponent("author")).getField().getText();
        String isbn = ((FieldComponent) this.finalFormBox.getComponent("isbn")).getField().getText();
        Editor editor = ((SearchFieldComponent<Editor>) this.finalFormBox.getComponent("editor")).getSelected();
        Category category = ((SearchFieldComponent<Category>) this.finalFormBox.getComponent("category")).getSelected();
        int quantity = ((IntSpinnerComponent) this.finalFormBox.getComponent("quantity")).getSpinner().getValue();
        int year = ((IntFieldComponent) this.finalFormBox.getComponent("year")).getField().getText().isBlank() ? 0 : Integer.parseInt(((IntFieldComponent) this.finalFormBox.getComponent("year")).getField().getText());

        if (title.isBlank() || author.isBlank() || isbn.isBlank() || editor == null || quantity == 0 || year == 0) {
            GuiUtil.alertError("Veuillez remplir tous les champs obligatoires");
            return;
        }

        this.getLibrary().createOeuvre(title, author, isbn, editor, category, quantity, year);
        this.getApp().getStageWrapper().setContent(new OeuvresScene(this.getApp()));
        SaveRunnable.create(this.getApp()).run();
    }

    private void initForms() {
        FieldComponent isbn = new FieldComponent("ISBN");
        isbn.getField().setOnAction(event -> this.consumeIsbnForm());

        this.isbnFormBox = new FormBox.Builder("Nouvelle oeuvre")
                .addComponent("isbn", isbn)
                .addButton("Mode manuel", event -> this.vBox.getChildren().setAll(this.finalFormBox))
                .addButton("Suivant", event -> this.consumeIsbnForm())
                .addButton("Annuler", event -> this.getApp().getStageWrapper().setContent(new OeuvresScene(this.getApp())))
                .build();

        ObservableList<Editor> editors = FXCollections.observableArrayList(this.getLibrary().getDocuments(SaveableType.EDITOR).stream().map(document -> (Editor) document).toList());
        ObservableList<Category> categories = FXCollections.observableArrayList(this.getLibrary().getDocuments(SaveableType.CATEGORY).stream().map(document -> (Category) document).toList());

        this.finalFormBox = new FormBox.Builder("Nouvelle oeuvre")
                .addComponent("title", new FieldComponent("Titre"))
                .addComponent("author", new FieldComponent("Auteur"))
                .addComponent("isbn", new FieldComponent("ISBN"))
                .addComponent("editor", new SearchFieldComponent<>("Éditeur", editors))
                .addComponent("category", new SearchFieldComponent<>("Catégorie", categories))
                .addComponent("quantity", new IntSpinnerComponent("Quantité"))
                .addComponent("year", new IntFieldComponent("Année"))
                .addButton("Sauvegarder", event -> this.consumeFinalForm())
                .addButton("Annuler", event -> this.getApp().getStageWrapper().setContent(new OeuvresScene(this.getApp())))
                .build();
    }
}
