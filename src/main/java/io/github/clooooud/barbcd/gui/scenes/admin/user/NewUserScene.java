package io.github.clooooud.barbcd.gui.scenes.admin.user;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.gui.element.FieldComponent;
import io.github.clooooud.barbcd.gui.element.FormBox;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;

public class NewUserScene extends RootAdminScene {

    private FormBox formBox;

    public NewUserScene(BarBCD app) {
        super(app);
    }

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);

        formBox = new FormBox.Builder("Nouvel utilisateur")
                .addComponent("name", new FieldComponent("Nom d'utilisateur"))
                .addComponent("password", new FieldComponent("Mot de passe", true))
                .addButton("Annuler", event -> this.getApp().getStageWrapper().setContent(new UsersScene(this.getApp())))
                .addButton("Sauvegarder", event -> consumeForm())
                .build();

        vBox.getChildren().add(formBox);
    }

    private void consumeForm() {
        String name = ((FieldComponent) formBox.getComponent("name")).getField().getText();
        String password = ((FieldComponent) formBox.getComponent("password")).getField().getText();

        if (name.isBlank() || password.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs !").showAndWait();
            return;
        }

        this.getLibrary().createUser(name, password, this.getLibrary().getAdminPassword());
        SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
        this.getApp().getStageWrapper().setContent(new UsersScene(this.getApp()));
    }
}
