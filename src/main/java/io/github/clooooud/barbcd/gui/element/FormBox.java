package io.github.clooooud.barbcd.gui.element;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormBox extends Box {

    private final Map<String, FormComponent> components = new HashMap<>();
    private final Map<String, Button> buttons = new HashMap<>();

    protected FormBox(String formName, String description, List<Map.Entry<String, FormComponent>> components, List<Map.Entry<String, EventHandler<ActionEvent>>> buttons) {
        super();
        initFormBox(formName, description, components, buttons);

        this.setOpacity(0);
        Platform.runLater(() -> this.setOpacity(1));
    }

    public Map<String, Button> getButtons() {
        return buttons;
    }

    public Map<String, FormComponent> getComponents() {
        return components;
    }

    public FormComponent getComponent(String componentName) {
        return components.get(componentName);
    }

    public Button getButton(String buttonName) {
        return buttons.get(buttonName);
    }

    private void initFormBox(String formName, String description, List<Map.Entry<String, FormComponent>> components, List<Map.Entry<String, EventHandler<ActionEvent>>> buttons) {
        Label titleLabel = new Label(formName);
        titleLabel.setWrapText(true);
        titleLabel.setFont(Font.font(null, FontWeight.BOLD, 28));
        this.vBox.getChildren().addAll(titleLabel, new Separator(Orientation.HORIZONTAL));

        initDescription(description);
        initComponents(components);
        initButtons(buttons);
    }

    private void initDescription(String description) {
        if (description != null) {
            Label descriptionLabel = new Label(description);
            descriptionLabel.setFont(Font.font(14));
            descriptionLabel.setWrapText(true);
            this.vBox.getChildren().addAll(descriptionLabel, new Separator(Orientation.HORIZONTAL));
        }
    }

    private void initComponents(List<Map.Entry<String, FormComponent>> components) {
        for (Map.Entry<String, FormComponent> component : components) {
            FormComponent formComponent = component.getValue();
            String componentName = component.getKey();

            this.vBox.getChildren().add(formComponent);
            this.components.put(componentName, formComponent);
        }
    }

    private void initButtons(List<Map.Entry<String, EventHandler<ActionEvent>>> buttons) {
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        for (Map.Entry<String, EventHandler<ActionEvent>> entry : buttons) {
            String buttonName = entry.getKey();
            EventHandler<ActionEvent> actionHandler = entry.getValue();

            Button formButton = new Button(buttonName);
            formButton.getStyleClass().add("form-button");
            formButton.setOnAction(actionHandler);

            buttonBox.getChildren().add(formButton);
            this.buttons.put(buttonName, formButton);
        }

        this.vBox.getChildren().add(buttonBox);
    }

    public static class Builder {

        private final List<Map.Entry<String, FormComponent>> components = new ArrayList<>();
        private final List<Map.Entry<String, EventHandler<ActionEvent>>> buttons = new ArrayList<>();

        private final String name;
        private String description;

        public Builder(String name) {
            this.name = name;
        }

        public Builder addButton(String buttonName, EventHandler<ActionEvent> actionHandler) {
            this.buttons.add(Map.entry(buttonName, actionHandler));
            return this;
        }

        public Builder addComponent(String componentName, FormComponent component) {
            this.components.add(Map.entry(componentName, component));
            return this;
        }

        public Builder setDesc(String description) {
            this.description = description;
            return this;
        }

        public FormBox build() {
            return new FormBox(name, description, components, buttons);
        }
    }
}
