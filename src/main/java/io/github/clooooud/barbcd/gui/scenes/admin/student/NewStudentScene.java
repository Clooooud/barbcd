package io.github.clooooud.barbcd.gui.scenes.admin.student;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.model.classes.Class;
import io.github.clooooud.barbcd.gui.element.SimpleFormBox;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class NewStudentScene extends RootAdminScene {

    private SimpleFormBox formBox;
    private final Class classObject;

    public NewStudentScene(BarBCD app, Class classObject) {
        super(app);
        this.classObject = classObject;
    }

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);

        formBox = new SimpleFormBox.Builder("Nouvel étudiant")
                .addField("Nom")
                .addField("Prénom")
                .addButton("Sauvegarder", event -> this.consumeForm())
                .addButton("Annuler", event -> this.getApp().getStageWrapper().setContent(new ClassScene(this.getApp(), classObject)))
                .build();

        vBox.getChildren().add(formBox);
    }

    private void consumeForm() {
        String lastName = formBox.getField("Nom").getText();
        String firstName = formBox.getField("Prénom").getText();

        if (lastName.isBlank() || firstName.isBlank()) {
            GuiUtil.alertError("Veuillez remplir tous les champs.");
            return;
        }

        this.getLibrary().createStudent(firstName, lastName, classObject);
        SaveRunnable.create(this.getApp()).run();
        this.getApp().getStageWrapper().setContent(new ClassScene(this.getApp(), classObject));
    }
}
