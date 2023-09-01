package io.github.clooooud.barbcd.gui.scenes.admin.editor;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.model.document.Editor;
import io.github.clooooud.barbcd.gui.element.SimpleFormBox;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.gui.scenes.admin.oeuvre.OeuvresScene;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class NewEditorScene extends RootAdminScene {

    protected SimpleFormBox formBox;
    protected String formName;

    public NewEditorScene(BarBCD app) {
        super(app);
        this.formName = "Nouvel éditeur";
    }

    @Override
    protected Class<?> getParentClass() {
        return EditorsScene.class;
    }

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);

        formBox = new SimpleFormBox.Builder(formName)
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
            GuiUtil.alertError("Un éditeur avec ce nom existe déjà !");
            return;
        }

        this.getLibrary().createEditor(name);
        SaveRunnable.create(this.getApp()).run();
        this.getApp().getStageWrapper().setContent(new EditorsScene(this.getApp()));
    }
}
