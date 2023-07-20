package io.github.clooooud.barbcd.gui.element;

import io.github.clooooud.barbcd.gui.scenes.RootScene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleFormBox extends FormBox {

    private SimpleFormBox(String formName, String description, List<Map.Entry<String, FormComponent>> components, List<Map.Entry<String, EventHandler<ActionEvent>>> buttons) {
        super(formName, description, components, buttons);

        for (FormComponent component : this.getComponents().values()) {
            FieldComponent fieldComponent = (FieldComponent) component;
            TextField textField = fieldComponent.getField();
            textField.textProperty().addListener(observable -> RootScene.validateNonEmptyTextField(textField));
        }
    }

    public Collection<TextField> getFields() {
        return getComponents().values().stream().map(component -> ((FieldComponent) component).getField()).collect(Collectors.toUnmodifiableSet());
    }

    public TextField getField(String fieldName) {
        return ((FieldComponent) getComponent(fieldName)).getField();
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

        public Builder addField(String fieldName) {
            return addField(fieldName, null);
        }

        public Builder addField(String fieldName, String description) {
            return addField(fieldName, description, false);
        }

        public Builder addField(String fieldName, boolean password) {
            return addField(fieldName, null, password);
        }

        public Builder addField(String fieldName, String description, boolean password) {
            this.components.add(Map.entry(fieldName, new FieldComponent(fieldName, description, password)));
            return this;
        }

        public Builder setDesc(String description) {
            this.description = description;
            return this;
        }

        public SimpleFormBox build() {
            return new SimpleFormBox(name, description, components, buttons);
        }
    }
}
