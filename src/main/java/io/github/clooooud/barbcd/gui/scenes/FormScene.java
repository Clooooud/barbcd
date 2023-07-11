package io.github.clooooud.barbcd.gui.scenes;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.gui.element.FormBox;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.Collection;
import java.util.List;

public abstract class FormScene extends RootScene {

    private FormBox formBox;

    public FormScene(BarBCD app) {
        super(app);
    }

    protected Button getButton(String buttonName) {
        return formBox.getButton(buttonName);
    }

    protected TextField getField(String fieldName) {
        return formBox.getField(fieldName);
    }

    protected Collection<TextField> getFields() {
        return formBox.getFields().values();
    }

    protected Collection<Button> getButtons() {
        return formBox.getButtons().values();
    }

    protected Runnable initButton(String buttonName, Button button) {
        return null;
    }

    protected abstract String getTitle();

    protected abstract String getDescription();

    protected abstract List<String> getFieldNames();

    protected abstract List<String> getPasswordFieldNames();

    protected abstract List<String> getButtonNames();

    @Override
    public void initContent(VBox vBox) {
        vBox.getStyleClass().add("form-page-content");

        this.formBox = new FormBox(
                getTitle(),
                getDescription(),
                getButtonNames(),
                getFieldNames(),
                getPasswordFieldNames()
        );
        this.formBox.setOpacity(0);

        vBox.getChildren().add(formBox);
        this.formBox.getButtons().forEach((buttonName, button) -> {
            Runnable runnable = initButton(buttonName, button);
            button.setOnAction(event -> runnable.run());
        });

        Platform.runLater(() -> this.formBox.setOpacity(1));
    }
}
