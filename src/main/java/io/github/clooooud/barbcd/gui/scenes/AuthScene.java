package io.github.clooooud.barbcd.gui.scenes;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.auth.User;
import io.github.clooooud.barbcd.gui.element.Popup;
import io.github.clooooud.barbcd.gui.element.SimpleFormBox;
import io.github.clooooud.barbcd.gui.element.components.FieldComponent;
import io.github.clooooud.barbcd.gui.element.components.TextComponent;
import io.github.clooooud.barbcd.gui.scenes.admin.MainAdminScene;
import io.github.clooooud.barbcd.util.AESUtil;
import io.github.clooooud.barbcd.util.GuiUtil;
import io.github.clooooud.barbcd.util.Sha256Util;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
            GuiUtil.alertError("Cet utilisateur n'existe pas.");
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
            GuiUtil.alertError("Le mot de passe est incorrect.");
            return;
        }

        this.getLibrary().setAdminPassword(decryptedPassword);
        this.getLibrary().setUser(user);
        this.getApp().getStageWrapper().setContent(new MainAdminScene(this.getApp()));

        // Test if admin key has changed without updating the encrypted file
        AESUtil currentAES = new AESUtil(decryptedPassword);
        try {
            currentAES.decrypt("pr_credentials.enc");
        } catch (Exception e) {
            UpdatePasswordPopup passwordPopup = new UpdatePasswordPopup("pr_credentials.enc");
            passwordPopup.showAndWait();

            String oldPassword = passwordPopup.oldPass;
            AESUtil oldAES = new AESUtil(oldPassword);
            String oldCredentials = oldAES.decrypt("pr_credentials.enc");
            currentAES.encrypt(oldCredentials, "pr_credentials.enc");
        }
    }

    private String getDescription() {
        return "Veuillez rentrer votre nom d'utilisateur et votre mot de passe. " +
                "Le nom d'utilisateur 'admin' est réservé pour l'administrateur.";
    }

    private static class UpdatePasswordPopup extends Popup {

        private final String file;
        private String oldPass = "";

        public UpdatePasswordPopup(String file) {
            super("Mise à jour du mot de passe");
            this.file = file;
        }

        @Override
        public Scene getPopupContent() {
            VBox vBox = new VBox();
            vBox.setPadding(new Insets(20));
            vBox.setSpacing(10);
            vBox.setAlignment(Pos.CENTER);

            TextComponent textComponent = new TextComponent("", "Un changement de mot de passe a été détecté,\nveuillez nous fournir l'ancien mot de passe pour\nmettre à jour le fichier encrypté");
            FieldComponent oldPassField = new FieldComponent("Ancien mot de passe");

            HBox buttonBox = new HBox();
            buttonBox.setSpacing(10);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);

            Button saveButton = new Button("Finaliser");
            saveButton.getStyleClass().add("form-button");
            saveButton.setOnAction(event -> consumeForm());

            buttonBox.getChildren().addAll(saveButton);
            vBox.getChildren().addAll(textComponent, oldPassField, buttonBox);

            // JavaFX decided that this value won't change with TextField#getText, so I had to do this
            oldPassField.getField().textProperty().addListener((observableValue, oldValue, newValue) -> oldPass = newValue);

            return new Scene(vBox);
        }

        private void consumeForm() {
            if (oldPass.isBlank()) {
                GuiUtil.alertError("Veuillez remplir tous les champs obligatoires");
                return;
            }

            try {
                new AESUtil(oldPass).decrypt(this.file);
            } catch (RuntimeException e) {
                GuiUtil.alertError("Mauvais ancien mot de passe");
                return;
            }

            GuiUtil.wrapAlert(new Alert(Alert.AlertType.INFORMATION, "Mot de passe valide !")).showAndWait();
            this.close();
        }
    }
}
