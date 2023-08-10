package io.github.clooooud.barbcd.gui.scenes.admin.editor;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.auth.User;
import io.github.clooooud.barbcd.data.model.classes.Class;
import io.github.clooooud.barbcd.data.model.document.Editor;
import io.github.clooooud.barbcd.gui.StageWrapper;
import io.github.clooooud.barbcd.gui.element.ScrollBox;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.gui.scenes.admin.user.NewUserScene;
import io.github.clooooud.barbcd.gui.scenes.admin.user.UserScene;
import io.github.clooooud.barbcd.gui.scenes.admin.user.UsersScene;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditorsScene extends RootAdminScene {

    public EditorsScene(BarBCD app) {
        super(app);
    }

    private final Map<CheckBox, Editor> checkBoxEditorMap = new HashMap<>();
    private final Map<Editor, HBox> userList = new HashMap<>();

    private VBox contentBox;
    private TextField filter;
    private HBox addButtonBox;
    private Button deleteButton;

    private List<Editor> getSelectedEditors() {
        return checkBoxEditorMap.entrySet().stream()
                .filter(entry -> entry.getKey().isSelected())
                .map(Map.Entry::getValue)
                .toList();
    }

    private void deleteSelectedEditors() {
        // TODO: gérer supprimer sans toucher au doc / supprimer aussi les doc
        GuiUtil.wrapAlert(new Alert(
                Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer ces éditeurs ?"
        )).showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData().isDefaultButton()) {
                getSelectedEditors().forEach(editor -> {
                    this.getLibrary().removeDocument(editor);
                    // gérer les documents liés
                });
                SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
                this.getApp().getStageWrapper().setContent(new EditorsScene(this.getApp()));
            }
        });
    }

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.getStyleClass().add("admin-content");

        Label label = new Label("Éditeurs");
        label.getStyleClass().add("admin-scene-title");

        vBox.getChildren().add(label);

        HBox utilBar = new HBox();
        utilBar.setSpacing(5);
        utilBar.setAlignment(Pos.CENTER);

        Label filterLabel = new Label("Filtre");
        filterLabel.setFont(Font.font(null, FontWeight.BOLD, 14));

        filter = new TextField();
        filter.setFocusTraversable(false);
        filter.setPromptText("Rechercher un éditeur");
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
        deleteButton.setOnAction(event -> deleteSelectedEditors());
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
                Editor user = entry.getKey();
                return user.getName().contains(filter.getText());
            }).sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).toList());
        }
        contentBox.getChildren().add(addButtonBox);
    }

    private VBox createContent() {
        VBox vBox = new VBox();
        vBox.setSpacing(10);

        List<Editor> list = this.getLibrary().getDocuments(SaveableType.EDITOR).stream()
                .map(document -> (Editor) document)
                .sorted().toList();

        for (int i = 0; i < list.size(); i++) {
            Editor editor = list.get(i);

            HBox editorLine = new HBox();
            editorLine.setAlignment(Pos.CENTER_LEFT);
            editorLine.setSpacing(10);
            editorLine.setPadding(new Insets(0, 0, 0, 10));

            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                deleteButton.setDisable(getSelectedEditors().isEmpty());
            });
            checkBoxEditorMap.put(checkBox, editor);

            HBox userBox = createEditorBox(editor);

            editorLine.getChildren().addAll(checkBox, userBox);
            HBox.setHgrow(userBox, Priority.ALWAYS);

            vBox.getChildren().add(editorLine);
            userList.put(editor, editorLine);
        }

        addButtonBox = new HBox();
        addButtonBox.setAlignment(Pos.CENTER);

        ImageView addButton = new ImageView(new Image(StageWrapper.getResource("assets/add.png")));
        addButton.setFitWidth(50);
        addButton.setFitHeight(50);
        addButton.setCursor(Cursor.HAND);
        addButton.setOnMouseClicked(event -> this.getApp().getStageWrapper().setContent(new NewEditorScene(this.getApp())));

        addButtonBox.getChildren().add(addButton);
        vBox.getChildren().add(addButtonBox);

        return vBox;
    }

    private void deleteEditor(Editor editor) {
        GuiUtil.wrapAlert(new Alert(
                Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer cet utilisateur ?"
        )).showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData().isDefaultButton()) {
                this.getLibrary().removeDocument(editor);
                // Gérer les documents liés
                SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
                this.getApp().getStageWrapper().setContent(new EditorsScene(this.getApp()));
            }
        });
    }

    private HBox createEditorBox(Editor editor) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("list-elem");

        VBox vBox = new VBox();
        vBox.setPrefHeight(50);
        vBox.setMinHeight(50);
        vBox.setMaxHeight(50);

        Label nameLabel = new Label(editor.getName());
        Label classLabel = new Label(getEditorString(editor));

        nameLabel.getStyleClass().add("list-elem-title");
        classLabel.getStyleClass().add("list-elem-content");

        vBox.getChildren().addAll(nameLabel, classLabel);
        hBox.getChildren().add(vBox);
        HBox.setHgrow(vBox, Priority.ALWAYS);

        HBox deleteButtonBox = new HBox();
        deleteButtonBox.setAlignment(Pos.CENTER);
        deleteButtonBox.setCursor(Cursor.HAND);
        deleteButtonBox.setOnMouseClicked(event -> deleteEditor(editor));

        ImageView deleteButton = new ImageView(new Image(StageWrapper.getResource("assets/x.png")));
        deleteButton.setFitWidth(25);
        deleteButton.setFitHeight(25);

        deleteButtonBox.getChildren().add(deleteButton);

        hBox.getChildren().add(deleteButtonBox);

        hBox.setOnMouseClicked(event -> {
            if (GuiUtil.isNodeClicked(event.getX(), event.getY(), deleteButtonBox)) {
                return;
            }

            this.getApp().getStageWrapper().setContent(new EditorScene(this.getApp(), editor));
        });

        return hBox;
    }

    private String getEditorString(Editor editor) {
        int documentCount = editor.getEditedDocuments(this.getLibrary()).size();

        String content;

        if (documentCount == 0) {
            content = "Cet éditeur n'est lié à aucun document.";
        } else {
            content = "Cet éditeur est lié à " + documentCount + " document" + (documentCount > 1 ? "s" : "") + ".";
        }

        return content;
    }
}
