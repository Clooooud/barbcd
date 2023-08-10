package io.github.clooooud.barbcd.gui.scenes.admin.user;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.auth.User;
import io.github.clooooud.barbcd.data.model.classes.Class;
import io.github.clooooud.barbcd.gui.StageWrapper;
import io.github.clooooud.barbcd.gui.element.ScrollBox;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersScene extends RootAdminScene {

    private final Map<CheckBox, User> checkBoxUserMap = new HashMap<>();
    private final Map<User, HBox> userList = new HashMap<>();

    private VBox contentBox;
    private TextField filter;
    private HBox addButtonBox;
    private Button deleteButton;

    public UsersScene(BarBCD app) {
        super(app);
    }

    private List<User> getSelectedUsers() {
        return checkBoxUserMap.entrySet().stream()
                .filter(entry -> entry.getKey().isSelected())
                .map(Map.Entry::getValue)
                .toList();
    }

    private void deleteSelectedUsers() {
        GuiUtil.wrapAlert(new Alert(
                Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer ces utilisateurs ?"
        )).showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData().isDefaultButton()) {
                getSelectedUsers().forEach(user -> {
                    this.getLibrary().removeDocument(user);
                    user.getResponsibilities(this.getLibrary()).forEach(responsibility -> this.getLibrary().removeDocument(responsibility));
                });
                SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
                this.getApp().getStageWrapper().setContent(new UsersScene(this.getApp()));
            }
        });
    }

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.getStyleClass().add("admin-content");

        Label label = new Label("Utilisateurs");
        label.getStyleClass().add("admin-scene-title");
        
        vBox.getChildren().add(label);

        HBox utilBar = new HBox();
        utilBar.setSpacing(5);
        utilBar.setAlignment(Pos.CENTER);

        Label filterLabel = new Label("Filtre");
        filterLabel.setFont(Font.font(null, FontWeight.BOLD, 14));

        filter = new TextField();
        filter.setFocusTraversable(false);
        filter.setPromptText("Rechercher un utilisateur");
        filter.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.strip().equals(newValue.strip())) {
                return;
            }

            updateContent();
        });

        deleteButton = new Button();
        ImageView imageView = new ImageView(new Image(StageWrapper.getResource("assets/trash-2.png")));
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        deleteButton.setGraphic(imageView);
        deleteButton.setCursor(Cursor.HAND);
        deleteButton.setOnAction(event -> deleteSelectedUsers());
        deleteButton.setDisable(true);

        utilBar.getChildren().addAll(filterLabel, filter, deleteButton);
        vBox.getChildren().add(utilBar);

        contentBox = createContent();
        ScrollBox scrollBox = new ScrollBox(vBox, contentBox, true);
        scrollBox.setMaxWidth(800);
        vBox.getChildren().add(scrollBox);
    }

    private void updateContent() {
        if (filter.getText().isBlank()) {
            contentBox.getChildren().setAll(userList.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).toList());
        } else {
            contentBox.getChildren().setAll(userList.entrySet().stream().filter(entry -> {
                User user = entry.getKey();
                return user.getLogin().contains(filter.getText());
            }).sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).toList());
        }
        contentBox.getChildren().add(addButtonBox);
    }

    private VBox createContent() {
        VBox vBox = new VBox();
        vBox.setSpacing(10);

        List<User> list = this.getLibrary().getUsers().stream().sorted().toList();
        for (int i = 0; i < list.size(); i++) {
            User user = list.get(i);

            HBox userLine = new HBox();
            userLine.setAlignment(Pos.CENTER_LEFT);
            userLine.setSpacing(10);
            userLine.setPadding(new Insets(0, 0, 0, 10));

            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                deleteButton.setDisable(getSelectedUsers().isEmpty());
            });
            checkBoxUserMap.put(checkBox, user);

            if (user.isAdmin()) {
                checkBox.setVisible(false);
                checkBox.setDisable(true);
            }

            HBox userBox = createUserBox(user);

            userLine.getChildren().addAll(checkBox, userBox);
            HBox.setHgrow(userBox, Priority.ALWAYS);

            vBox.getChildren().add(userLine);
            userList.put(user, userLine);
        }

        addButtonBox = new HBox();
        addButtonBox.setAlignment(Pos.CENTER);

        ImageView addButton = new ImageView(new Image(StageWrapper.getResource("assets/add.png")));
        addButton.setFitWidth(50);
        addButton.setFitHeight(50);
        addButton.setCursor(Cursor.HAND);
        addButton.setOnMouseClicked(event -> this.getApp().getStageWrapper().setContent(new NewUserScene(this.getApp())));

        addButtonBox.getChildren().add(addButton);
        vBox.getChildren().add(addButtonBox);

        return vBox;
    }

    private void deleteUser(User user) {
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
        deleteButtonBox.setOnMouseClicked(event -> deleteUser(user));

        ImageView deleteButton = new ImageView(new Image(StageWrapper.getResource("assets/x.png")));
        deleteButton.setFitWidth(25);
        deleteButton.setFitHeight(25);

        deleteButtonBox.getChildren().add(deleteButton);
        
        hBox.getChildren().add(deleteButtonBox);

        hBox.setOnMouseClicked(event -> {
            if (GuiUtil.isNodeClicked(event.getX(), event.getY(), deleteButtonBox)) {
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
