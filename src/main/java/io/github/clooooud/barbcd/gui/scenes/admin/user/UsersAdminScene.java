package io.github.clooooud.barbcd.gui.scenes.admin.user;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.auth.User;
import io.github.clooooud.barbcd.data.model.classes.Class;
import io.github.clooooud.barbcd.gui.element.ScrollBox;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

public class UsersAdminScene extends RootAdminScene {

    public UsersAdminScene(BarBCD app) {
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

        List<User> list = this.getLibrary().getUsers().stream().sorted().toList();
        for (int i = 0; i < list.size(); i++) {
            User user = list.get(i);

            VBox userBox = createUserBox(user);

            if (i == 0) {
                userBox.getStyleClass().add("list-elem-first");
            } else if (i == list.size()-1) {
                userBox.getStyleClass().add("list-elem-last");
            }

            userBox.setOnMouseClicked(event -> this.getApp().getStageWrapper().setContent(new UserAdminScene(this.getApp(), user)));

            vBox.getChildren().add(userBox);
        }

        return vBox;
    }

    private VBox createUserBox(User user) {
        VBox vBox = new VBox();

        vBox.setPrefHeight(50);
        vBox.setMinHeight(50);
        vBox.setMaxHeight(50);
        vBox.getStyleClass().add("list-elem");
        vBox.getStyleClass().add("list-elem-" + ((user.getId() % 2 == 0) ? "even" : "odd"));

        Label nameLabel = new Label(user.getLogin());
        Label classLabel = new Label(getClassString(user));

        nameLabel.getStyleClass().add("list-elem-title");
        classLabel.getStyleClass().add("list-elem-content");

        vBox.getChildren().addAll(nameLabel, classLabel);

        return vBox;
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
