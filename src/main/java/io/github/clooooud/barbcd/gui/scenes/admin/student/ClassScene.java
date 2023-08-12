package io.github.clooooud.barbcd.gui.scenes.admin.student;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.model.classes.Class;
import io.github.clooooud.barbcd.data.model.classes.Student;
import io.github.clooooud.barbcd.data.model.document.Borrowing;
import io.github.clooooud.barbcd.gui.scenes.admin.ListAdminScene;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.scene.control.Alert;

import java.util.List;

public class ClassScene extends ListAdminScene<Student> {

    private final Class classObject;

    public ClassScene(BarBCD app, Class classObject) {
        super(app);
        this.classObject = classObject;
    }

    @Override
    protected List<Student> getObjects() {
        return this.getLibrary().getDocuments(SaveableType.STUDENT).stream()
                .map(document -> (Student) document)
                .filter(student -> student.getCurrentClass().equals(this.classObject))
                .sorted().toList();
    }

    @Override
    protected void massDelete() {
        GuiUtil.wrapAlert(new Alert(
                Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer ces élèves ? Supprimer un élève supprime aussi ses emprunts."
        )).showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData().isDefaultButton()) {
                getSelectedObjects().forEach(object -> {
                    this.getLibrary().removeDocument(object);
                    this.getLibrary().getDocuments(SaveableType.BORROWING).stream()
                            .map(document -> (Borrowing) document)
                            .filter(borrowing -> borrowing.getStudent().equals(object))
                            .forEach(borrowing -> this.getLibrary().removeDocument(borrowing));
                });
                SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
                this.getApp().getStageWrapper().setContent(new ClassScene(this.getApp(), this.classObject));
            }
        });
    }

    @Override
    protected void deleteObject(Student object) {
        GuiUtil.wrapAlert(new Alert(
                Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer cet élève ? Supprimer un élève supprime aussi ses emprunts."
        )).showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData().isDefaultButton()) {
                this.getLibrary().removeDocument(object);
                this.getLibrary().getDocuments(SaveableType.BORROWING).stream()
                        .map(document -> (Borrowing) document)
                        .filter(borrowing -> borrowing.getStudent().equals(object))
                        .forEach(borrowing -> this.getLibrary().removeDocument(borrowing));
                SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
                this.getApp().getStageWrapper().setContent(new ClassScene(this.getApp(), this.classObject));
            }
        });
    }

    @Override
    protected String getTitle() {
        return "Classe - " + this.classObject.getClassName();
    }

    @Override
    protected String getFilterPrompt() {
        return "Rechercher un élève";
    }

    @Override
    protected String getFilterString(Student object) {
        return object.getFirstName() + " " + object.getLastName();
    }

    @Override
    protected RootAdminScene getNewObjectScene() {
        return new NewStudentScene(this.getApp(), this.classObject);
    }

    @Override
    protected RootAdminScene getObjectScene(Student object) {
        // Redirection page emprunt avec le filtre déjà rempli
        return this;
    }

    @Override
    protected String getListObjectName(Student object) {
        return object.getFirstName() + " " + object.getLastName();
    }

    @Override
    protected String getListObjectDesc(Student object) {
        long borrowCount = this.getLibrary().getDocuments(SaveableType.BORROWING).stream()
                .map(document -> (Borrowing) document)
                .filter(borrowing -> borrowing.getStudent().equals(object))
                .filter(borrowing -> !borrowing.isFinished())
                .count();
        return borrowCount + " emprunt" + (borrowCount > 1 ? "s" : "") + " en cours";
    }

    @Override
    protected boolean canDeleteObject(Student object) {
        return false;
    }
}
