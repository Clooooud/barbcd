package io.github.clooooud.barbcd.gui.scenes;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.auth.User;
import io.github.clooooud.barbcd.gui.scenes.admin.MainAdminScene;
import io.github.clooooud.barbcd.util.AESUtil;
import io.github.clooooud.barbcd.util.Sha256Util;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.HBox;

import java.util.Collections;
import java.util.List;

public class AuthScene extends FormScene {

    public AuthScene(BarBCD app) {
        super(app);
    }

    @Override
    public HBox getHeader() {
        HBox header = super.getHeader();

        getAuthButton().setOnAction((event) -> getFields().forEach(TextInputControl::clear));

        return header;
    }

    @Override
    protected Runnable getButtonAction(String buttonName, Button button) {
        return this::consumeForm;
    }

    @Override
    protected void initField(String fieldName, TextField field) {
        field.setOnAction(event -> consumeForm());
    }

    private void consumeForm() {
        if (!this.getFields().stream().allMatch(this::validateNonEmptyTextField)) {
            return;
        }

        String login = this.getField("Utilisateur").getText();
        String password = this.getField("Mot de passe").getText();

        User user = this.getLibrary().getUser(login);

        if (user == null) {
            new Alert(Alert.AlertType.ERROR, "Cet utilisateur n'existe pas.").showAndWait();
            return;
        }

        boolean goodPassword = user.getPasswordHash().equals(Sha256Util.passToSha256(password));

        if (!goodPassword) {
            new Alert(Alert.AlertType.ERROR, "Le mot de passe est incorrect.").showAndWait();
            return;
        }

        String decryptedPassword = password;

        if (!user.isAdmin()) {
            AESUtil aesUtil = new AESUtil(password);
            decryptedPassword = aesUtil.decryptString(user.getMainPassword());
        }

        this.getLibrary().setAdminPassword(decryptedPassword);
        this.getLibrary().setUser(user);
        this.getApp().getStageWrapper().setContent(new MainAdminScene(this.getApp()));

    }

    @Override
    protected String getTitle() {
        return "Authentification";
    }

    @Override
    protected String getDescription() {
        return "Veuillez rentrer votre nom d'utilisateur et votre mot de passe. Le nom d'utilisateur 'admin' est réservé pour l'administrateur.";
    }

    @Override
    protected List<String> getFieldNames() {
        return List.of("Utilisateur", "Mot de passe");
    }

    @Override
    protected List<String> getPasswordFieldNames() {
        return List.of("Mot de passe");
    }

    @Override
    protected List<String> getButtonNames() {
        return Collections.singletonList("Se connecter");
    }
}
