package io.github.clooooud.barbcd.gui.scenes.admin.editor;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.model.document.Editor;
import io.github.clooooud.barbcd.gui.element.SimpleFormBox;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;

public class NewEditorScene extends RootAdminScene {

    protected SimpleFormBox formBox;

    public NewEditorScene(BarBCD app) {
        super(app);
    }

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);

        formBox = new SimpleFormBox.Builder("Nouvel éditeur")
                .addField("Nom de l'éditeur")
                .addButton("Sauvegarder", event -> this.consumeForm())
                .addButton("Annuler", event -> this.getApp().getStageWrapper().setContent(new EditorsScene(this.getApp())))
                .build();

        vBox.getChildren().add(formBox);
    }

    protected void consumeForm() {
        String name = formBox.getField("Nom de l'éditeur").getText();

        if (name.isBlank()) {
            return;
        }

        if (this.getLibrary().getDocuments(SaveableType.EDITOR).stream()
                .map(document -> (Editor) document)
                .anyMatch(editor -> editor.getName().equalsIgnoreCase(name))) {
            GuiUtil.wrapAlert(new Alert(Alert.AlertType.ERROR, "Un éditeur avec ce nom existe déjà !")).showAndWait();
            return;
        }

        this.getLibrary().createEditor(name);
        SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
        this.getApp().getStageWrapper().setContent(new EditorsScene(this.getApp()));
    }
}
