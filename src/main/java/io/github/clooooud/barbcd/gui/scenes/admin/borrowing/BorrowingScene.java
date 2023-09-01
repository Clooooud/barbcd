package io.github.clooooud.barbcd.gui.scenes.admin.borrowing;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.model.classes.Student;
import io.github.clooooud.barbcd.data.model.document.Borrowing;
import io.github.clooooud.barbcd.data.model.document.ViewableDocument;
import io.github.clooooud.barbcd.gui.element.FormBox;
import io.github.clooooud.barbcd.gui.element.components.TextComponent;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.gui.scenes.admin.oeuvre.OeuvresScene;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class BorrowingScene extends RootAdminScene {

    private final Borrowing borrowing;

    public BorrowingScene(BarBCD app, Borrowing borrowing) {
        super(app);
        this.borrowing = borrowing;
    }

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);

        ViewableDocument document = borrowing.getBorrowedDocument();
        String documentText = document.getTitle() + " - " + document.getAuthor();

        Student student = borrowing.getStudent();
        String studentText = student.getCurrentClass().getClassName() + " - " + student.getFirstName() + " " + student.getLastName();

        FormBox formBox = new FormBox.Builder("Emprunt")
                .addComponent("Document", new TextComponent("Document", documentText))
                .addComponent("Étudiant", new TextComponent("Étudiant", studentText))
                .addButton("Finir l'emprunt", event -> endBorrowing())
                .build();

        vBox.getChildren().add(formBox);
    }

    private void endBorrowing() {
        this.getLibrary().removeDocument(borrowing);
        this.getApp().getStageWrapper().setContent(new BorrowingsScene(this.getApp()));
        SaveRunnable.create(this.getApp()).run();
    }

    @Override
    protected Class<?> getParentClass() {
        return BorrowingsScene.class;
    }
}
