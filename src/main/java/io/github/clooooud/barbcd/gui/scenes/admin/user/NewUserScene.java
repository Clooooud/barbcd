package io.github.clooooud.barbcd.gui.scenes.admin.user;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.gui.element.SimpleFormBox;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;

public class NewUserScene extends RootAdminScene {

    private SimpleFormBox formBox;

    public NewUserScene(BarBCD app) {
        super(app);
    }

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);

        formBox = new SimpleFormBox.Builder("Nouvel utilisateur")
                .addField("Nom d'utilisateur")
                .addField("Mot de passe", true)
                .addButton("Sauvegarder", event -> consumeForm())
                .addButton("Annuler", event -> this.getApp().getStageWrapper().setContent(new UsersScene(this.getApp())))
                .build();

        vBox.getChildren().add(formBox);
    }

    private void consumeForm() {
        String name = formBox.getField("Nom d'utilisateur").getText();
        String password = formBox.getField("Mot de passe").getText();

        if (name.isBlank() || password.isBlank()) {
            GuiUtil.wrapAlert(new Alert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs !")).showAndWait();
            return;
        }

        if (this.getLibrary().getUser(name) != null) {
            GuiUtil.wrapAlert(new Alert(Alert.AlertType.ERROR, "Un utilisateur avec ce nom existe déjà !")).showAndWait();
            return;
        }

        this.getLibrary().createUser(name, password, this.getLibrary().getAdminPassword());
        SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
        this.getApp().getStageWrapper().setContent(new UsersScene(this.getApp()));
    }
}
