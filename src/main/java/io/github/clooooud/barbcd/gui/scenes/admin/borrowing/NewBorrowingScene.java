package io.github.clooooud.barbcd.gui.scenes.admin.borrowing;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.model.classes.Student;
import io.github.clooooud.barbcd.gui.element.FieldComponent;
import io.github.clooooud.barbcd.gui.element.FormBox;
import io.github.clooooud.barbcd.gui.element.SearchFieldComponent;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class NewBorrowingScene extends RootAdminScene {

    private FormBox formBox;
    private FieldComponent isbn;
    private SearchFieldComponent<Student> studentSearchField;

    public NewBorrowingScene(BarBCD app) {
        super(app);
    }

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);

        ObservableList<Student> students = FXCollections.observableArrayList(this.getLibrary().getDocuments(SaveableType.STUDENT).stream().map(document -> (Student) document).toList())
                .sorted((student, student2) -> student.toString().compareToIgnoreCase(student2.toString()));

        isbn = new FieldComponent("ISBN du document");
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
            return;
        }

        // TODO: need document creation before (magazine + oeuvre)
    }
}
