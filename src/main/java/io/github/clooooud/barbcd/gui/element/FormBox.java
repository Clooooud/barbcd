package io.github.clooooud.barbcd.gui.element;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormBox extends VBox {

    private final Map<String, TextField> fields = new HashMap<>();
    private final Map<String, Button> buttons = new HashMap<>();

    public FormBox(String formName, String description, List<String> buttonNames, List<String> fieldNames, List<String> passwordFieldNames) {
        initFormBox(formName, description, buttonNames, fieldNames, passwordFieldNames);
    }

    public Map<String, Button> getButtons() {
        return buttons;
    }

    public Map<String, TextField> getFields() {
        return fields;
    }

    public TextField getField(String fieldName) {
        return fields.get(fieldName);
    }

    public Button getButton(String buttonName) {
        return buttons.get(buttonName);
    }

    private void initFormBox(String formName, String description, List<String> buttonNames, List<String> fieldNames, List<String> passwordFieldNames) {
        this.getStyleClass().add("form-box");
        Platform.runLater(() -> this.maxWidthProperty().bind(Bindings.min(600, this.getScene().widthProperty().subtract(300))));

        Label titleLabel = new Label(formName);
        titleLabel.setWrapText(true);
        titleLabel.setFont(Font.font(null, FontWeight.BOLD, 28));
        this.getChildren().addAll(titleLabel, new Separator(Orientation.HORIZONTAL));

        if (description != null) {
            Label descriptionLabel = new Label(description);
            descriptionLabel.setFont(Font.font(14));
            descriptionLabel.setWrapText(true);
            this.getChildren().addAll(descriptionLabel, new Separator(Orientation.HORIZONTAL));
        }

        for (String fieldName : fieldNames) {
            fields.put(fieldName, initField(fieldName, passwordFieldNames.contains(fieldName)));
        }


        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        for (String buttonName : buttonNames) {
            Button formButton = new Button(buttonName);
            formButton.getStyleClass().add("form-button");
            buttonBox.getChildren().add(formButton);
            buttons.put(buttonName, formButton);
        }

        this.getChildren().add(buttonBox);
    }

    private TextField initField(String fieldName, boolean password) {
        VBox fieldBox = new VBox();

        Label label = new Label(fieldName);
        label.setFont(Font.font(null, FontWeight.BOLD, 16));

        TextField field = password ? new PasswordField() : new TextField();

        fieldBox.getChildren().addAll(label, field);
        this.getChildren().add(fieldBox);

        return field;
    }
}
