package io.github.clooooud.barbcd.gui.scenes;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.api.GSheetApi;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.auth.AdminUser;
import io.github.clooooud.barbcd.util.AESUtil;
import io.github.clooooud.barbcd.util.Sha256Util;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.net.URL;
import java.util.List;

public class StartScene extends FormScene {

    private static void openWebpage(String urlString) {
        try {
            Desktop.getDesktop().browse(new URL(urlString).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public StartScene(BarBCD app) {
        super(app);
    }

    @Override
    protected Runnable getButtonAction(String buttonName, Button button) {
        return switch (buttonName) {
            case "Tutoriel" -> this::openTutorial;
            case "Finaliser" -> () -> {
                if (consumeForm()) {
                    this.getApp().getStageWrapper().setContent(new MainScene(this.getApp()));
                }
            };
            default -> null;
        };
    }

    private void openTutorial() {
        openWebpage("https://www.google.com");
    }

    private boolean consumeForm() {
        if (!this.getFields().stream().allMatch(this::validateNonEmptyTextField)) {
            return false;
        }

        String password = this.getField("Mot de passe Admin").getText();
        String privateCredentials = this.getField("Compte de service Google").getText();
        String apiKey = this.getField("Clé API Google").getText();

        AESUtil aesUtil = new AESUtil(password);
        aesUtil.encrypt(privateCredentials, GSheetApi.PRIVATE_CREDENTIAL_PATH_NAME);

        this.getApp().getCredentials().setApiKey(apiKey);
        this.getApp().getCredentials().save();
        this.getLibrary().addDocument(new AdminUser(Sha256Util.passToSha256(password)));

        SaveRunnable.start(this.getLibrary(), this.getApp().getGSheetApi(), password);

        return true;
    }

    @Override
    public void initContent(VBox vBox) {
        super.initContent(vBox);
    }

    @Override
    protected String getTitle() {
        return "Bienvenue dans BarBCD !";
    }

    @Override
    protected String getDescription() {
        return "Pour utiliser cette application, vous devez y entrer des données importantes. " +
                "Nous utilisons Google Drive comme base de donnée et par conséquent certains paramétrages sont nécessaires. " +
                "Veuillez suivre le tutoriel si besoin.";
    }

    @Override
    protected List<String> getFieldNames() {
        return List.of("Mot de passe Admin", "Compte de service Google", "Clé API Google");
    }

    @Override
    protected List<String> getPasswordFieldNames() {
        return List.of("Mot de passe Admin");
    }

    @Override
    protected List<String> getButtonNames() {
        return List.of("Tutoriel", "Finaliser");
    }


}
