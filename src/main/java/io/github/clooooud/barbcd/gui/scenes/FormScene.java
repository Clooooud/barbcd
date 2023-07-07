package io.github.clooooud.barbcd.gui.scenes;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.gui.element.FormBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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

    protected void initButton(String buttonName, Button button) {

    }

    protected abstract String getTitle();

    protected abstract String getDescription();

    protected abstract List<String> getFieldNames();

    protected abstract List<String> getButtonNames();

    @Override
    public void initContent(VBox vBox) {
        vBox.getStyleClass().add("form-page-content");

        this.formBox = new FormBox(
                getTitle(),
                getDescription(),
                getButtonNames(),
                getFieldNames()
        );

        vBox.getChildren().add(formBox);
        this.formBox.getButtons().forEach(this::initButton);
    }
}