package io.github.clooooud.barbcd.gui.scenes.admin.student;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.model.classes.Class;
import io.github.clooooud.barbcd.data.model.classes.Responsibility;
import io.github.clooooud.barbcd.data.model.document.Borrowing;
import io.github.clooooud.barbcd.gui.scenes.admin.ListAdminScene;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.scene.control.Alert;

import java.util.List;

public class ClassesScene extends ListAdminScene<Class> {

    public ClassesScene(BarBCD app) {
        super(app);
    }

    @Override
    protected List<Class> getObjects() {
        List<Class> classList = this.getLibrary().getDocuments(SaveableType.CLASS).stream()
                .map(document -> (Class) document)
                .sorted().toList();
        if (this.getLibrary().getUser().isAdmin()) {
            return classList;
        }

        return classList.stream().filter(classObject -> this.getLibrary().getDocuments(SaveableType.RESPONSIBILITY).stream()
                .map(document -> (Responsibility) document)
                .anyMatch(responsibility -> responsibility.getOwnedClass().equals(classObject) && responsibility.getUser().equals(this.getLibrary().getUser()))).toList();
    }

    @Override
    protected void massDelete() {
        GuiUtil.wrapAlert(new Alert(
                Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer ces classes ? Supprimer des classes en masse supprime aussi leurs élèves et leurs emprunts."
        )).showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData().isDefaultButton()) {
                getSelectedObjects().forEach(object -> {
                    this.getLibrary().removeDocument(object);
                    object.getStudents().forEach(student -> {
                        this.getLibrary().removeDocument(student);
                        this.getLibrary().getDocuments(SaveableType.BORROWING).stream()
                                .map(document -> (Borrowing) document)
                                .filter(borrowing -> borrowing.getStudent().equals(student))
                                .forEach(borrowing -> this.getLibrary().removeDocument(borrowing));
                        this.getLibrary().getDocuments(SaveableType.RESPONSIBILITY).stream()
                                .map(document -> (Responsibility) document)
                                .filter(responsibility -> responsibility.getOwnedClass().equals(object))
                                .forEach(responsibility -> this.getLibrary().removeDocument(responsibility));
                    });
                });
                SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
                this.getApp().getStageWrapper().setContent(new ClassesScene(this.getApp()));
            }
        });
    }

    @Override
    protected void deleteObject(Class object) {
        GuiUtil.wrapAlert(new Alert(
                Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer cette classe ? Supprimer une classe supprime aussi ses élèves et leurs emprunts."
        )).showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData().isDefaultButton()) {
                this.getLibrary().removeDocument(object);
                object.getStudents().forEach(student -> {
                    this.getLibrary().removeDocument(student);
                    this.getLibrary().getDocuments(SaveableType.BORROWING).stream()
                            .map(document -> (Borrowing) document)
                            .filter(borrowing -> borrowing.getStudent().equals(student))
                            .forEach(borrowing -> this.getLibrary().removeDocument(borrowing));
                    this.getLibrary().getDocuments(SaveableType.RESPONSIBILITY).stream()
                            .map(document -> (Responsibility) document)
                            .filter(responsibility -> responsibility.getOwnedClass().equals(object))
                            .forEach(responsibility -> this.getLibrary().removeDocument(responsibility));
                });
                SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
                this.getApp().getStageWrapper().setContent(new ClassesScene(this.getApp()));
            }
        });
    }

    @Override
    protected String getTitle() {
        return "Classes";
    }

    @Override
    protected String getFilterPrompt() {
        return "Rechercher une classe";
    }

    @Override
    protected String getFilterString(Class object) {
        return object.getClassName();
    }

    @Override
    protected RootAdminScene getNewObjectScene() {
        return new NewClassScene(this.getApp());
    }

    @Override
    protected RootAdminScene getObjectScene(Class object) {
        return new ClassScene(this.getApp(), object);
    }

    @Override
    protected String getListObjectName(Class object) {
        return object.getClassName();
    }

    @Override
    protected String getListObjectDesc(Class object) {
        int studentCount = object.getStudents().size();
        return studentCount + " élève" + (studentCount > 1 ? "s" : "");
    }

    @Override
    protected boolean canDeleteObject(Class object) {
        return this.getLibrary().getUser().isAdmin();
    }

}
