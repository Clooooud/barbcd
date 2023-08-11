package io.github.clooooud.barbcd.gui.scenes.admin.student;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.model.classes.Class;
import io.github.clooooud.barbcd.data.model.classes.Responsibility;
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
    protected HBox createObjectBox(Class object) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("list-elem");

        VBox vBox = new VBox();
        vBox.setPrefHeight(50);
        vBox.setMinHeight(50);
        vBox.setMaxHeight(50);

        Label nameLabel = new Label(object.getClassName());
        Label classLabel = new Label(getClassString(object));

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

            this.getApp().getStageWrapper().setContent(getObjectScene(object));
        });

        return hBox;
    }

    private String getClassString(Class object) {
        int studentCount = object.getStudents().size();
        return studentCount + " élève" + (studentCount > 1 ? "s" : "");
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

}
