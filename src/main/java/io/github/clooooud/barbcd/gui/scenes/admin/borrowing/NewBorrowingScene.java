package io.github.clooooud.barbcd.gui.scenes.admin.borrowing;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.model.classes.Student;
import io.github.clooooud.barbcd.data.model.document.Borrowing;
import io.github.clooooud.barbcd.data.model.document.ViewableDocument;
import io.github.clooooud.barbcd.gui.element.FormBox;
import io.github.clooooud.barbcd.gui.element.components.SearchFieldComponent;
import io.github.clooooud.barbcd.gui.element.components.ValidableFieldComponent;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.gui.scenes.admin.oeuvre.OeuvresScene;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class NewBorrowingScene extends RootAdminScene {

    private FormBox formBox;
    private ValidableFieldComponent isbn;
    private SearchFieldComponent<Student> studentSearchField;

    public NewBorrowingScene(BarBCD app) {
        super(app);
    }

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);

        ObservableList<Student> students = FXCollections.observableArrayList(this.getLibrary().getDocuments(SaveableType.STUDENT).stream().map(document -> (Student) document).toList())
                .sorted((student, student2) -> student.toString().compareToIgnoreCase(student2.toString()));

        ValidableFieldComponent.Validable validable = string -> getLibrary().getViewableDocuments().stream().anyMatch(document -> document.getISBN().equalsIgnoreCase(string));

        isbn = new ValidableFieldComponent("ISBN du document", validable);
        studentSearchField = new SearchFieldComponent<>("Étudiant", students);

        this.formBox = new FormBox.Builder("Nouvel emprunt")
                .addComponent("isbn", isbn)
                .addComponent("student", studentSearchField)
                .addButton("Sauvegarder", event -> this.consumeForm())
                .addButton("Annuler", event -> this.getApp().getStageWrapper().setContent(new BorrowingsScene(this.getApp())))
                .build();

        vBox.getChildren().add(formBox);
    }

    private void consumeForm() {
        String isbn = this.isbn.getField().getText();
        Student student = this.studentSearchField.getSelected();

        if (isbn.isBlank() || student == null) {
            GuiUtil.alertError("Veuillez remplir tous les champs.");
            return;
        }

        ViewableDocument document = this.getLibrary().getViewableDocuments().stream().filter(doc -> doc.getISBN().equalsIgnoreCase(isbn)).findFirst().orElse(null);

        if (document == null) {
            GuiUtil.alertError("Aucun document trouvé avec cet ISBN.");
            return;
        }

        if (!document.isAvailable(this.getLibrary())) {
            GuiUtil.alertError("Ce document est déjà emprunté.");
            return;
        }

        Borrowing borrowing = this.getLibrary().createBorrowing(document, student);
        this.getApp().getStageWrapper().setContent(new BorrowingScene(this.getApp(), borrowing));
        SaveRunnable.create(this.getApp()).run();
    }

    @Override
    protected Class<?> getParentClass() {
        return BorrowingsScene.class;
    }
}
