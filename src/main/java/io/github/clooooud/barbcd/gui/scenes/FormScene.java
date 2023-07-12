package io.github.clooooud.barbcd.gui.scenes;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.gui.element.FormBox;
import javafx.application.Platform;
import javafx.scene.Node;
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

    protected Runnable getButtonAction(String buttonName, Button button) {
        return null;
    }

    protected void initField(String fieldName, TextField field) {

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
            Runnable runnable = getButtonAction(buttonName, button);
            button.setOnAction(event -> runnable.run());
        });

        this.formBox.getFields().forEach((fieldName, field) -> {
            this.initField(fieldName, field);
            field.textProperty().addListener(event -> validateNonEmptyTextField(field));
        });

        Platform.runLater(() -> this.formBox.setOpacity(1));
    }

    public static void markNodeErrorStatus(Node node, boolean isValid) {
        if (isValid) {
            node.setStyle(null);
        } else {
            node.setStyle("-fx-control-inner-background: f8d7da");
        }
    }


    protected boolean validateNonEmptyTextField(TextField textField) {
        boolean isValid = textField.getText().strip().length() > 0;

        markNodeErrorStatus(textField, isValid);

        return isValid;
    }

}
