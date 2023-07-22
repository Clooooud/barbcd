package io.github.clooooud.barbcd.gui.scenes;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.auth.User;
import io.github.clooooud.barbcd.gui.element.SimpleFormBox;
import io.github.clooooud.barbcd.gui.scenes.admin.MainAdminScene;
import io.github.clooooud.barbcd.util.AESUtil;
import io.github.clooooud.barbcd.util.Sha256Util;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.crypto.BadPaddingException;

public class AuthScene extends RootScene {

    private SimpleFormBox formBox;

    public AuthScene(BarBCD app) {
        super(app);
    }

    @Override
    public void initContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);

        this.formBox = new SimpleFormBox.Builder("Authentification")
                .setDesc(getDescription())
                .addField("Utilisateur")
                .addField("Mot de passe", true)
                .addButton("Se connecter", (event) -> consumeForm())
                .build();

        this.formBox.getFields().forEach(this::initField);

        vBox.getChildren().add(this.formBox);
    }

    @Override
    public HBox getHeader() {
        HBox header = super.getHeader();

        getAuthButton().setOnAction((event) -> this.formBox.getFields().forEach(TextInputControl::clear));

        return header;
    }

    protected void initField(TextField field) {
        field.setOnAction(event -> consumeForm());
    }

    private void consumeForm() {
        if (!this.formBox.getFields().stream().allMatch(RootScene::validateNonEmptyTextField)) {
            return;
        }

        String login = this.formBox.getField("Utilisateur").getText();
        String password = this.formBox.getField("Mot de passe").getText();

        User user = this.getLibrary().getUser(login);

        if (user == null) {
            new Alert(Alert.AlertType.ERROR, "Cet utilisateur n'existe pas.").showAndWait();
            return;
        }

        String decryptedPassword = " ";

        if (!user.isAdmin()) {
            AESUtil aesUtil = new AESUtil(password);
            try {
                decryptedPassword = aesUtil.decryptString(user.getMainPassword());
            } catch (Exception ignored) {}
        } else {
            decryptedPassword = password;
        }

        String adminPasswordHash = this.getLibrary().getUser("admin").getPassword();
        String hashToTest = Sha256Util.passToSha256(decryptedPassword);
        boolean goodPassword = adminPasswordHash.equals(hashToTest);

        if (!goodPassword) {
            new Alert(Alert.AlertType.ERROR, "Le mot de passe est incorrect.").showAndWait();
            return;
        }

        this.getLibrary().setAdminPassword(decryptedPassword);
        this.getLibrary().setUser(user);
        this.getApp().getStageWrapper().setContent(new MainAdminScene(this.getApp()));

    }

    private String getDescription() {
        return "Veuillez rentrer votre nom d'utilisateur et votre mot de passe. " +
                "Le nom d'utilisateur 'admin' est réservé pour l'administrateur.";
    }
}
