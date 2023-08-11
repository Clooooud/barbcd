package io.github.clooooud.barbcd.gui.scenes.admin.student;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.model.classes.Class;
import io.github.clooooud.barbcd.data.model.classes.Student;
import io.github.clooooud.barbcd.data.model.document.Borrowing;
import io.github.clooooud.barbcd.gui.StageWrapper;
import io.github.clooooud.barbcd.gui.scenes.admin.ListAdminScene;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

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
    protected HBox createObjectBox(Student object) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("list-elem");

        VBox vBox = new VBox();
        vBox.setPrefHeight(50);
        vBox.setMinHeight(50);
        vBox.setMaxHeight(50);

        Label nameLabel = new Label(object.getFirstName() + " " + object.getLastName());
        Label classLabel = new Label(getStudentString(object));

        nameLabel.getStyleClass().add("list-elem-title");
        classLabel.getStyleClass().add("list-elem-content");

        vBox.getChildren().addAll(nameLabel, classLabel);
        hBox.getChildren().add(vBox);
        HBox.setHgrow(vBox, Priority.ALWAYS);

        HBox deleteButtonBox = new HBox();
        deleteButtonBox.setAlignment(Pos.CENTER);
        deleteButtonBox.setCursor(Cursor.HAND);
        deleteButtonBox.setOnMouseClicked(event -> deleteObject(object));

        ImageView deleteButton = new ImageView(new Image(StageWrapper.getResource("assets/x.png")));
        deleteButton.setFitWidth(25);
        deleteButton.setFitHeight(25);

        if (this.getLibrary().getUser().isAdmin()) {
            deleteButtonBox.getChildren().add(deleteButton);
            hBox.getChildren().add(deleteButtonBox);
        }

        hBox.setOnMouseClicked(event -> {
            if (this.getLibrary().getUser().isAdmin()) {
                if (GuiUtil.isNodeClicked(event.getX(), event.getY(), deleteButtonBox)) {
                    return;
                }
            }

            //this.getApp().getStageWrapper().setContent(getObjectScene(object));
        });

        return hBox;
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

    private String getStudentString(Student student) {
        long borrowCount = this.getLibrary().getDocuments(SaveableType.BORROWING).stream()
                .map(document -> (Borrowing) document)
                .filter(borrowing -> borrowing.getStudent().equals(student))
                .filter(borrowing -> !borrowing.isFinished())
                .count();
        return borrowCount + " emprunt" + (borrowCount > 1 ? "s" : "") + " en cours";
    }

    @Override
    protected RootAdminScene getNewObjectScene() {
        return new NewStudentScene(this.getApp(), this.classObject);
    }

    @Override
    protected RootAdminScene getObjectScene(Student object) {
        // Redirection page emprunt avec le filtre déjà rempli
        return null;
    }
}
