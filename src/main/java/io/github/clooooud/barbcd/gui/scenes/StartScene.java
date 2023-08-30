package io.github.clooooud.barbcd.gui.scenes;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.api.GSheetApi;
import io.github.clooooud.barbcd.data.api.tasks.RunnableWrapper;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.auth.AdminUser;
import io.github.clooooud.barbcd.gui.element.SimpleFormBox;
import io.github.clooooud.barbcd.util.AESUtil;
import io.github.clooooud.barbcd.util.GuiUtil;
import io.github.clooooud.barbcd.util.Sha256Util;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class StartScene extends RootScene {

    private SimpleFormBox formBox;

    public StartScene(BarBCD app) {
        super(app);
    }

    @Override
    protected void homeButtonAction(MouseEvent event) {

    }

    @Override
    protected void authButtonAction(ActionEvent event) {

    }

    @Override
    public void initContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);

        this.formBox = new SimpleFormBox.Builder("Bienvenue dans BarBCD !")
                .setDesc(getDescription())
                .addField("Mot de passe Admin",true)
                .addField("Compte de service Google", "Copiez le contenu du fichier json directement")
                .addField("Clé API Google")
                .addButton("Finaliser", (event) -> consumeForm())
                .addButton("Tutoriel", (event) -> openTutorial())
                .build();

        vBox.getChildren().add(this.formBox);
    }

    private void openTutorial() {
        openWebpage("https://www.google.com");
    }

    private void openWebpage(String urlString) {
        try {
            Desktop.getDesktop().browse(new URL(urlString).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void consumeForm() {
        if (!this.formBox.getFields().stream().allMatch(RootScene::validateNonEmptyTextField)) {
            return;
        }

        String password = this.formBox.getField("Mot de passe Admin").getText();
        String privateCredentials = this.formBox.getField("Compte de service Google").getText();
        String apiKey = this.formBox.getField("Clé API Google").getText();

        AESUtil aesUtil = new AESUtil(password);
        aesUtil.encrypt(privateCredentials, GSheetApi.PRIVATE_CREDENTIAL_PATH_NAME);

        this.getApp().getCredentials().setApiKey(apiKey);
        this.getApp().getCredentials().save();
        this.getLibrary().addDocument(new AdminUser(Sha256Util.passToSha256(password)));
        this.getLibrary().markDocumentAsUpdated(this.getLibrary());

        GSheetApi gSheetApi = this.getApp().getGSheetApi();
        AtomicBoolean result = new AtomicBoolean(false);
        new RunnableWrapper(() -> {
            try {
                gSheetApi.initAdmin(password);
                gSheetApi.reset();
                result.set(true);
                SaveRunnable.create(this.getApp()).run(false);
            } catch (Exception e) {
                e.printStackTrace();
                GuiUtil.alertError("Une erreur s'est produite lors d'un appel à l'API de Google. Une des clés est mauvaise.");
            }
        }).then(gSheetApi::closeAdminMode).run(false);

        if (!result.get()) {
            return;
        }

        GuiUtil.wrapAlert(new Alert(Alert.AlertType.INFORMATION, "La création des bases de données a été réalisée")).showAndWait();
        this.getApp().getStageWrapper().setContent(new MainScene(this.getApp()));
    }

    private String getDescription() {
        return "Pour utiliser cette application, vous devez y entrer des données importantes. " +
                "Nous utilisons Google Drive comme base de donnée et par conséquent certains paramétrages sont nécessaires. " +
                "Veuillez suivre le tutoriel si besoin.";
    }
}
