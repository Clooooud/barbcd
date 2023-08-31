package io.github.clooooud.barbcd.gui.scenes.admin.oeuvre;

import com.google.api.services.books.model.Volume;
import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.GBookApi;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.model.document.Category;
import io.github.clooooud.barbcd.data.model.document.Editor;
import io.github.clooooud.barbcd.gui.element.FormBox;
import io.github.clooooud.barbcd.gui.element.Popup;
import io.github.clooooud.barbcd.gui.element.components.*;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
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
        String year = book.getPublishedDate().substring(0, Math.min(4, book.getPublishedDate().length()));
        String editorString = book.getPublisher();
        Editor editor = this.getLibrary().getDocuments(SaveableType.EDITOR).stream()
                .map(document -> (Editor) document)
                .filter(editor1 -> editor1.getName().equalsIgnoreCase(editorString))
                .findFirst().orElse(null);

        if (editor == null && editorString != null && !editorString.isBlank()) {
            ButtonType buttonType = GuiUtil.wrapAlert(new Alert(Alert.AlertType.CONFIRMATION, "L'éditeur " + editorString + " n'existe pas, voulez-vous le créer ?"))
                    .showAndWait()
                    .orElse(null);

            if (buttonType != null && buttonType == ButtonType.OK) {
                editor = this.getLibrary().createEditor(editorString);
                ((AddSearchFieldComponent<Editor>) this.finalFormBox.getComponent("editor")).getSearchField().getItems().add(editor);
            }
        }

        ((FieldComponent) this.finalFormBox.getComponent("title")).getField().setText(title);
        ((FieldComponent) this.finalFormBox.getComponent("author")).getField().setText(author);
        ((FieldComponent) this.finalFormBox.getComponent("isbn")).getField().setText(isbn);
        ((IntFieldComponent) this.finalFormBox.getComponent("year")).getField().setText(year);

        if (editor != null) {
            ((AddSearchFieldComponent<Editor>) this.finalFormBox.getComponent("editor")).getSearchField().getSelectionModel().select(editor);
        }

        this.vBox.getChildren().setAll(this.finalFormBox);
    }

    private void consumeFinalForm() {
        // Retrieve every variables then create the oeuvre
        String title = ((FieldComponent) this.finalFormBox.getComponent("title")).getField().getText();
        String author = ((FieldComponent) this.finalFormBox.getComponent("author")).getField().getText();
        String isbn = ((FieldComponent) this.finalFormBox.getComponent("isbn")).getField().getText();
        Editor editor = ((AddSearchFieldComponent<Editor>) this.finalFormBox.getComponent("editor")).getSelected();
        Category category = ((AddSearchFieldComponent<Category>) this.finalFormBox.getComponent("category")).getSelected();
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

    private void startNewEditorPopup() {
        AddEditorPopup addEditorPopup = new AddEditorPopup();
        addEditorPopup.showAndWait();

        if (addEditorPopup.createdEditor != null) {
            ((AddSearchFieldComponent<Editor>) this.finalFormBox.getComponent("editor")).getSearchField().getItems().add(addEditorPopup.createdEditor);
            ((AddSearchFieldComponent<Editor>) this.finalFormBox.getComponent("editor")).getSearchField().getSelectionModel().select(addEditorPopup.createdEditor);
        }
    }

    private void startNewCategoryPopup() {
        AddCategoryPopup addCategoryPopup = new AddCategoryPopup();
        addCategoryPopup.showAndWait();

        if (addCategoryPopup.createdCategory != null) {
            ((AddSearchFieldComponent<Category>) this.finalFormBox.getComponent("category")).getSearchField().getItems().add(addCategoryPopup.createdCategory);
            ((AddSearchFieldComponent<Category>) this.finalFormBox.getComponent("category")).getSearchField().getSelectionModel().select(addCategoryPopup.createdCategory);
        }
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
                .addComponent("editor", new AddSearchFieldComponent<>("Éditeur", editors, (event) -> startNewEditorPopup()))
                .addComponent("category", new AddSearchFieldComponent<>("Catégorie", categories, (event) -> startNewCategoryPopup()))
                .addComponent("quantity", new IntSpinnerComponent("Quantité"))
                .addComponent("year", new IntFieldComponent("Année"))
                .addButton("Sauvegarder", event -> this.consumeFinalForm())
                .addButton("Annuler", event -> this.getApp().getStageWrapper().setContent(new OeuvresScene(this.getApp())))
                .build();
    }

    private class AddCategoryPopup extends Popup {

        private String categoryName = "";
        private SearchFieldComponent<Category> categorySearchField;
        private Category createdCategory;

        public AddCategoryPopup() {
            super("Ajouter une catégorie");
        }

        @Override
        public Scene getPopupContent() {
            VBox vBox = new VBox();
            vBox.setPadding(new Insets(20));
            vBox.setSpacing(10);
            vBox.setAlignment(Pos.CENTER);

            FieldComponent categoryField = new FieldComponent("Nom de la catégorie");

            ObservableList<Category> categories = FXCollections.observableArrayList(NewOeuvreScene.this.getLibrary().getDocuments(SaveableType.CATEGORY).stream().map(document -> (Category) document).toList());
            this.categorySearchField = new SearchFieldComponent<>("Catégorie parente (Non-obligatoire)", categories);

            HBox buttonBox = new HBox();
            buttonBox.setSpacing(10);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);

            Button saveButton = new Button("Sauvegarder");
            saveButton.getStyleClass().add("form-button");
            saveButton.setOnAction(event -> consumeForm());

            Button cancelButton = new Button("Annuler");
            cancelButton.getStyleClass().add("form-button");
            cancelButton.setOnAction(event -> this.close());

            buttonBox.getChildren().addAll(saveButton, cancelButton);
            vBox.getChildren().addAll(categoryField, categorySearchField, buttonBox);

            // JavaFX decided that this value won't change with TextField#getText, so I had to do this
            categoryField.getField().textProperty().addListener((observableValue, oldValue, newValue) -> categoryName = newValue);

            return new Scene(vBox);
        }

        private void consumeForm() {
            Category parent = categorySearchField.getSelected();

            if (categoryName.isBlank()) {
                GuiUtil.alertError("Veuillez remplir tous les champs obligatoires");
                return;
            }

            if (NewOeuvreScene.this.getLibrary().getDocuments(SaveableType.EDITOR).stream()
                    .map(document -> (Editor) document)
                    .anyMatch(editor -> editor.getName().equalsIgnoreCase(categoryName))) {
                GuiUtil.alertError("Une catégorie avec ce nom existe déjà !");
                return;
            }

            this.createdCategory = NewOeuvreScene.this.getLibrary().createCategory(categoryName, parent);
            this.close();
        }
    }

    private class AddEditorPopup extends Popup {

        private String editorName = "";
        private Editor createdEditor;

        public AddEditorPopup() {
            super("Ajouter un éditeur");
        }

        @Override
        public Scene getPopupContent() {
            VBox vBox = new VBox();
            vBox.setPadding(new Insets(20));
            vBox.setSpacing(10);
            vBox.setAlignment(Pos.CENTER);

            FieldComponent editorField = new FieldComponent("Nom de l'éditeur");

            HBox buttonBox = new HBox();
            buttonBox.setSpacing(10);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);

            Button saveButton = new Button("Sauvegarder");
            saveButton.getStyleClass().add("form-button");
            saveButton.setOnAction(event -> consumeForm());

            Button cancelButton = new Button("Annuler");
            cancelButton.getStyleClass().add("form-button");
            cancelButton.setOnAction(event -> this.close());

            buttonBox.getChildren().addAll(saveButton, cancelButton);
            vBox.getChildren().addAll(editorField, buttonBox);

            // JavaFX decided that this value won't change with TextField#getText, so I had to do this
            editorField.getField().textProperty().addListener((observableValue, oldValue, newValue) -> editorName = newValue);

            return new Scene(vBox);
        }

        private void consumeForm() {
            if (editorName.isBlank()) {
                GuiUtil.alertError("Veuillez remplir tous les champs obligatoires");
                return;
            }

            if (NewOeuvreScene.this.getLibrary().getDocuments(SaveableType.EDITOR).stream()
                    .map(document -> (Editor) document)
                    .anyMatch(editor -> editor.getName().equalsIgnoreCase(editorName))) {
                GuiUtil.alertError("Un éditeur avec ce nom existe déjà !");
                return;
            }

            this.createdEditor = NewOeuvreScene.this.getLibrary().createEditor(editorName);
            this.close();
        }
    }
}
