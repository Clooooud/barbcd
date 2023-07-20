package io.github.clooooud.barbcd.gui.scenes.admin;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.GSheetApi;
import io.github.clooooud.barbcd.data.api.tasks.RunnableWrapper;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.auth.AdminUser;
import io.github.clooooud.barbcd.data.model.Library;
import io.github.clooooud.barbcd.gui.element.Box;
import io.github.clooooud.barbcd.gui.element.FieldComponent;
import io.github.clooooud.barbcd.gui.element.FormBox;
import io.github.clooooud.barbcd.gui.element.FormComponent;
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
    private TextField nameField;
    private boolean hasNameChanged = false;

    public SettingsAdminScene(BarBCD app) {
        super(app);
    }

    @Override
    public void onSceneLeft() {
        if (!hasNameChanged) {
            return;
        }

        SaveRunnable.create(getLibrary(), getApp().getGSheetApi(), getLibrary().getAdminPassword()).run();
    }

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);

        Button ctaNode = new Button("Remise à zéro");
        ctaNode.getStyleClass().add("form-button");
        ctaNode.setOnAction((event -> onResetButtonClicked()));

        FieldComponent bcdName = new FieldComponent("Nom de la BCD");
        this.nameField = bcdName.getField();
        this.nameField.setText(this.getLibrary().getName());
        this.nameField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal.strip().length() > 10) {
                this.nameField.setText(oldVal);
                return;
            }

            hasNameChanged = true;
            this.getLibrary().setName(newVal.strip());
            updateHeader();
        });

        this.formBox = new FormBox.Builder("Paramètres")
                .setDesc("Cette interface est réservée aux administrateurs qui ont besoin d'effectuer des manipulations qui peuvent être risquées. " +
                        "Veuillez faire attention lors de son utilisation.")
                .addComponent("BCD Name", bcdName)
                .addComponent("Reset", new FormComponent(
                        "Remise à zéro",
                        ctaNode,
                        "Supprime TOUTES LES INFORMATIONS de la base."
                ))
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
                library.getDataUpdateList().clear();
                library.addDocument(new AdminUser(Sha256Util.passToSha256(adminPassword)));

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
