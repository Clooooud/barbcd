package io.github.clooooud.barbcd.gui.scenes.admin.editor;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.model.document.Editor;
import io.github.clooooud.barbcd.gui.scenes.admin.ListAdminScene;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

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

    @Override
    protected String getListObjectName(Editor object) {
        return object.getName();
    }

    @Override
    protected String getListObjectDesc(Editor object) {
        int documentCount = object.getEditedDocuments(this.getLibrary()).size();

        String content;

        if (documentCount == 0) {
            content = "Cet éditeur n'est lié à aucun document.";
        } else {
            content = "Cet éditeur est lié à " + documentCount + " document" + (documentCount > 1 ? "s" : "") + ".";
        }

        return content;
    }

    @Override
    protected boolean canDeleteObject(Editor object) {
        return true;
    }
}
