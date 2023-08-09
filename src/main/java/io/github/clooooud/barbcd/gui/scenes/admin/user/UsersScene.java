package io.github.clooooud.barbcd.gui.scenes.admin.user;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.auth.User;
import io.github.clooooud.barbcd.data.model.classes.Class;
import io.github.clooooud.barbcd.data.model.classes.Responsibility;
import io.github.clooooud.barbcd.gui.StageWrapper;
import io.github.clooooud.barbcd.gui.element.ScrollBox;
import io.github.clooooud.barbcd.gui.scenes.MainScene;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import javafx.geometry.Insets;
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

public class UsersScene extends RootAdminScene {

    public UsersScene(BarBCD app) {
        super(app);
    }

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.getStyleClass().add("admin-content");

        Label label = new Label("Utilisateurs");
        label.getStyleClass().add("admin-scene-title");
        
        vBox.getChildren().add(label);

        VBox content = createContent();
        ScrollBox scrollBox = new ScrollBox(vBox, content, true);
        scrollBox.setMaxWidth(800);
        vBox.getChildren().add(scrollBox);
    }

    private VBox createContent() {
        VBox vBox = new VBox();
        vBox.setSpacing(10);

        List<User> list = this.getLibrary().getUsers().stream().sorted().toList();
        for (int i = 0; i < list.size(); i++) {
            User user = list.get(i);

            HBox userBox = createUserBox(user);

            vBox.getChildren().add(userBox);
        }

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);

        ImageView addButton = new ImageView(new Image(StageWrapper.getResource("assets/add.png")));
        addButton.setFitWidth(50);
        addButton.setFitHeight(50);
        addButton.setCursor(Cursor.HAND);
        addButton.setOnMouseClicked(event -> this.getApp().getStageWrapper().setContent(new NewUserScene(this.getApp())));

        hBox.getChildren().add(addButton);
        vBox.getChildren().add(hBox);

        return vBox;
    }

    private HBox createUserBox(User user) {
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
        deleteButtonBox.setOnMouseClicked(event -> {
            new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Voulez-vous vraiment supprimer cet utilisateur ?"
            ).showAndWait().ifPresent(buttonType -> {
                if (buttonType.getButtonData().isDefaultButton()) {
                    this.getLibrary().removeDocument(user);
                    user.getResponsibilities(this.getLibrary()).forEach(responsibility -> this.getLibrary().removeDocument(responsibility));
                    SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
                    this.getApp().getStageWrapper().setContent(new UsersScene(this.getApp()));
                }
            });
        });

        ImageView deleteButton = new ImageView(new Image(StageWrapper.getResource("assets/x.png")));
        deleteButton.setFitWidth(25);
        deleteButton.setFitHeight(25);

        deleteButtonBox.getChildren().add(deleteButton);
        
        hBox.getChildren().add(deleteButtonBox);

        hBox.setOnMouseClicked(event -> {
            if (MainScene.isNodeClicked(event.getX(), event.getY(), deleteButtonBox)) {
                return;
            }

            this.getApp().getStageWrapper().setContent(new UserScene(this.getApp(), user));
        });

        return hBox;
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
