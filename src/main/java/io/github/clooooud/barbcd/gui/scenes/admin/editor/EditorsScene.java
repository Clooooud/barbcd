package io.github.clooooud.barbcd.gui.scenes.admin.editor;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.Saveable;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.model.document.Editor;
import io.github.clooooud.barbcd.gui.scenes.admin.ListAdminScene;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;

import java.util.List;

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
    protected void delete(Editor editor) {
        this.getLibrary().removeDocument(editor);
        editor.getEditedDocuments(this.getLibrary()).forEach(document -> this.getLibrary().removeDocument((Saveable) document));
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
    protected RootAdminScene getRefreshedScene() {
        return new EditorsScene(this.getApp());
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
    protected String getDeleteObjectMessage() {
        return "Voulez-vous vraiment supprimer cet éditeur ? Supprimer un éditeur supprime aussi ses documents associés.";
    }

    @Override
    protected String getDeleteObjectsMessage() {
        return "Voulez-vous vraiment supprimer ces éditeurs ? Supprimer des éditeurs en masse supprime aussi leurs documents associés.";
    }
}
