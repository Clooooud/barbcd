package io.github.clooooud.barbcd.gui.scenes.admin.editor;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.model.document.Editor;
import io.github.clooooud.barbcd.gui.StageWrapper;
import io.github.clooooud.barbcd.gui.scenes.admin.ListAdminScene;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class EditorsScene extends ListAdminScene<Editor> {

    public EditorsScene(BarBCD app) {
        super(app);
    }

    @Override
    protected List<Editor> getObjects() {
        return this.getLibrary().getDocuments(SaveableType.EDITOR).stream()
                .map(document -> (Editor) document)
                .sorted().toList();
    }

    @Override
    protected void massDelete() {
        GuiUtil.wrapAlert(new Alert(
                Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer ces éditeurs ? Supprimer des éditeurs en masse supprime aussi leurs documents associés."
        )).showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData().isDefaultButton()) {
                getSelectedObjects().forEach(editor -> {
                    this.getLibrary().removeDocument(editor);
                    editor.getEditedDocuments(this.getLibrary()).forEach(document -> this.getLibrary().removeDocument((Saveable) document));
                });
                SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
                this.getApp().getStageWrapper().setContent(new EditorsScene(this.getApp()));
            }
        });
    }

    @Override
    protected void deleteObject(Editor editor) {
        Optional<ButtonType> buttonType;

        if (!editor.getEditedDocuments(this.getLibrary()).isEmpty()) {
            buttonType = GuiUtil.wrapAlert(new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Cet éditeur est lié à des documents. Voulez-vous supprimer les documents qui sont liés ou annuler ?"
            )).showAndWait();
        } else {
            buttonType = GuiUtil.wrapAlert(new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Voulez-vous vraiment supprimer cet éditeur ?"
            )).showAndWait();
        }

        if (buttonType.isEmpty()) {
            return;
        }

        if (buttonType.get().getButtonData().isCancelButton()) {
            return;
        }

        editor.getEditedDocuments(this.getLibrary()).forEach(document -> this.getLibrary().removeDocument((Saveable) document));
        this.getLibrary().removeDocument(editor);
        SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
        this.getApp().getStageWrapper().setContent(new EditorsScene(this.getApp()));
    }

    protected HBox createObjectBox(Editor editor) {
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
        deleteButtonBox.setOnMouseClicked(event -> deleteObject(editor));

        ImageView deleteButton = new ImageView(new Image(StageWrapper.getResource("assets/x.png")));
        deleteButton.setFitWidth(25);
        deleteButton.setFitHeight(25);

        deleteButtonBox.getChildren().add(deleteButton);

        hBox.getChildren().add(deleteButtonBox);

        hBox.setOnMouseClicked(event -> {
            if (GuiUtil.isNodeClicked(event.getX(), event.getY(), deleteButtonBox)) {
                return;
            }

            this.getApp().getStageWrapper().setContent(getObjectScene(editor));
        });

        return hBox;
    }

    @Override
    protected String getTitle() {
        return "Éditeurs";
    }

    @Override
    protected String getFilterPrompt() {
        return "Rechercher un éditeur";
    }

    @Override
    protected String getFilterString(Editor object) {
        return object.getName();
    }

    @Override
    protected RootAdminScene getNewObjectScene() {
        return new NewEditorScene(this.getApp());
    }

    @Override
    protected RootAdminScene getObjectScene(Editor object) {
        return new EditorScene(this.getApp(), object);
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
