package io.github.clooooud.barbcd.gui.scenes.admin.user;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.auth.User;
import io.github.clooooud.barbcd.data.model.classes.Class;
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

public class UsersScene extends ListAdminScene<User> {

    public UsersScene(BarBCD app) {
        super(app);
    }

    @Override
    protected List<User> getObjects() {
        return this.getLibrary().getUsers().stream()
                .sorted().toList();
    }

    @Override
    protected void massDelete() {
        GuiUtil.wrapAlert(new Alert(
                Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer ces utilisateurs ?"
        )).showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData().isDefaultButton()) {
                getSelectedObjects().forEach(user -> {
                    this.getLibrary().removeDocument(user);
                    user.getResponsibilities(this.getLibrary()).forEach(responsibility -> this.getLibrary().removeDocument(responsibility));
                });
                SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
                this.getApp().getStageWrapper().setContent(new UsersScene(this.getApp()));
            }
        });
    }

    protected void deleteObject(User user) {
        GuiUtil.wrapAlert(new Alert(
                Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer cet utilisateur ?"
        )).showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData().isDefaultButton()) {
                this.getLibrary().removeDocument(user);
                user.getResponsibilities(this.getLibrary()).forEach(responsibility -> this.getLibrary().removeDocument(responsibility));
                SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
                this.getApp().getStageWrapper().setContent(new UsersScene(this.getApp()));
            }
        });
    }

    protected HBox createObjectBox(User user) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("list-elem");

        VBox vBox = new VBox();
        vBox.setPrefHeight(50);
        vBox.setMinHeight(50);
        vBox.setMaxHeight(50);

        Label nameLabel = new Label(user.getLogin());
        Label classLabel = new Label(getClassString(user));

        nameLabel.getStyleClass().add("list-elem-title");
        classLabel.getStyleClass().add("list-elem-content");

        vBox.getChildren().addAll(nameLabel, classLabel);
        hBox.getChildren().add(vBox);
        HBox.setHgrow(vBox, Priority.ALWAYS);

        if (user.isAdmin()) {
            hBox.setOnMouseClicked(event -> this.getApp().getStageWrapper().setContent(new UserScene(this.getApp(), user)));
            return hBox;
        }

        HBox deleteButtonBox = new HBox();
        deleteButtonBox.setAlignment(Pos.CENTER);
        deleteButtonBox.setCursor(Cursor.HAND);
        deleteButtonBox.setOnMouseClicked(event -> deleteObject(user));

        ImageView deleteButton = new ImageView(new Image(StageWrapper.getResource("assets/x.png")));
        deleteButton.setFitWidth(25);
        deleteButton.setFitHeight(25);

        deleteButtonBox.getChildren().add(deleteButton);
        
        hBox.getChildren().add(deleteButtonBox);

        hBox.setOnMouseClicked(event -> {
            if (GuiUtil.isNodeClicked(event.getX(), event.getY(), deleteButtonBox)) {
                return;
            }

            this.getApp().getStageWrapper().setContent(getObjectScene(user));
        });

        return hBox;
    }

    @Override
    protected String getTitle() {
        return "Utilisateurs";
    }

    @Override
    protected String getFilterPrompt() {
        return "Rechercher un utilisateur";
    }

    @Override
    protected String getFilterString(User object) {
        return object.getLogin();
    }

    @Override
    protected RootAdminScene getNewObjectScene() {
        return new NewUserScene(this.getApp());
    }

    @Override
    protected RootAdminScene getObjectScene(User object) {
        return new UserScene(this.getApp(), object);
    }

    private String getClassString(User user) {
        if (user.isAdmin()) {
            return "Cet utilisateur a accès à toutes les classes.";
        }

        List<String> classesName = user.getOwnedClasses(this.getLibrary())
                .stream()
                .map(Class::getClassName)
                .toList();

        String content;

        if (classesName.isEmpty()) {
            content = "Cet utilisateur n'a aucune classe assignée.";
        } else {
            content = "Classe" + (classesName.size() > 1 ? "s" : "") + " assignée" + (classesName.size() > 1 ? "s" : "") + " : " +
                    String.join(", ", classesName);
        }

        return content;
    }
}
