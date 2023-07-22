package io.github.clooooud.barbcd.gui.scenes.admin;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.GSheetApi;
import io.github.clooooud.barbcd.data.api.PublicCredentials;
import io.github.clooooud.barbcd.data.api.tasks.RunnableWrapper;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.auth.AdminUser;
import io.github.clooooud.barbcd.data.model.Library;
import io.github.clooooud.barbcd.gui.element.*;
import io.github.clooooud.barbcd.util.Sha256Util;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;

public class SettingsAdminScene extends RootAdminScene {

    private FormBox formBox;
    private TextField apiKeyField;

    private boolean hasNameChanged = false;
    private boolean hasApiKeyChanged = false;

    public SettingsAdminScene(BarBCD app) {
        super(app);
    }

    @Override
    public void onSceneLeft() {
        if (hasNameChanged) {
            SaveRunnable.create(getLibrary(), getApp().getGSheetApi(), getLibrary().getAdminPassword()).run();
        }

        if (hasApiKeyChanged) {
            ButtonType buttonType = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Voulez-vous vraiment mettre à jour la clé API ? Si vous n'avez pas de sauvegarde de l'ancienne clé, vous aurez sûrement des problèmes !"
            ).showAndWait().orElse(null);

            if (buttonType != null && buttonType.getButtonData().isDefaultButton()) {
                PublicCredentials credentials = this.getApp().getCredentials();
                credentials.setApiKey(apiKeyField.getText());
                credentials.save();
            }
        }
    }

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);

        FieldComponent bcdName = new FieldComponent("Nom de la BCD");
        TextField nameField = bcdName.getField();
        nameField.setText(this.getLibrary().getName().strip());
        nameField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal.strip().length() > 10) {
                nameField.setText(oldVal);
                return;
            }

            hasNameChanged = true;
            this.getLibrary().setName(newVal.strip());
            updateHeader();
        });

        FieldComponent apiKey = new FieldComponent(
                "Clé API Google",
                "La clé API créé sur l'interface de gestion de projet Google"
        );
        this.apiKeyField = apiKey.getField();
        this.apiKeyField.setText(this.getApp().getCredentials().getApiKey());
        this.apiKeyField.textProperty().addListener((observableValue, oldVal, newVal) -> hasApiKeyChanged = true);

        //TODO service account

        this.formBox = new FormBox.Builder("Paramètres")
                .setDesc("Cette interface est réservée aux administrateurs qui ont besoin d'effectuer des manipulations qui peuvent être risquées. " +
                        "Veuillez faire attention lors de son utilisation.")
                .addComponent("BCD Name", bcdName)
                .addComponent("Reset", new ButtonComponent(
                        "Remise à zéro",
                        "Remettre à zéro",
                        event -> onResetButtonClicked(),
                        "Supprime TOUTES LES INFORMATIONS de la base."
                ))
                .addComponent("API Key", apiKey)
                .build();

        vBox.getChildren().add(this.formBox);
    }

    private void onResetButtonClicked() {
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment remettre à zéro la base ?").showAndWait();

        if (buttonType.isEmpty()) {
            return;
        }

        if (buttonType.get().getButtonData().isCancelButton()) {
            return;
        }

        RunnableWrapper runnable = new RunnableWrapper(() -> {
            try {
                Library library = this.getLibrary();
                String adminPassword = library.getAdminPassword();
                this.getApp().getGSheetApi().initAdmin(adminPassword);
                this.getApp().getGSheetApi().reset();

                for (SaveableType type : SaveableType.values()) {
                    library.getDocuments(type).clear();
                }
                for (GSheetApi.RequestType requestType : GSheetApi.RequestType.values()) {
                    library.getDataUpdateList().get(requestType).clear();
                }

                library.addDocument(new AdminUser(Sha256Util.passToSha256(adminPassword)));
                library.addDocument(library);

                this.getApp().getGSheetApi().save(library);
                Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, "La base a été remise à zéro.").show());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                this.getApp().getStageWrapper().getStage().getScene().setCursor(Cursor.DEFAULT);
            }
        });

        this.getApp().getStageWrapper().getStage().getScene().setCursor(Cursor.WAIT);
        runnable.run();
    }
}
