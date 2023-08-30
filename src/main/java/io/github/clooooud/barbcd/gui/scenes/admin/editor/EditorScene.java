package io.github.clooooud.barbcd.gui.scenes.admin.editor;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.model.document.Editor;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.scene.layout.VBox;

public class EditorScene extends NewEditorScene {

    private final Editor editor;

    public EditorScene(BarBCD app, Editor editor) {
        super(app);
        this.editor = editor;
        this.formName = "Éditeur - " + editor.getName();
    }

    @Override
    public void initAdminContent(VBox vBox) {
        super.initAdminContent(vBox);
        formBox.getField("Nom de l'éditeur").setText(editor.getName());
    }

    @Override
    protected void consumeForm() {
        String name = formBox.getField("Nom de l'éditeur").getText();

        if (name.isBlank()) {
            return;
        }

        if (this.getLibrary().getDocuments(SaveableType.EDITOR).stream()
                .map(document -> (Editor) document)
                .anyMatch(editor -> editor.getName().equalsIgnoreCase(name) && !editor.equals(this.editor))) {
            GuiUtil.alertError("Un éditeur avec ce nom existe déjà !");
            return;
        }

        editor.setName(name);
        this.getLibrary().markDocumentAsUpdated(editor);
        SaveRunnable.create(this.getApp()).run();
        this.getApp().getStageWrapper().setContent(new EditorsScene(this.getApp()));
    }
}
