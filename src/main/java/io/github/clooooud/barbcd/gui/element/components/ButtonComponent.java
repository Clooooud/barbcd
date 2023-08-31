package io.github.clooooud.barbcd.gui.element.components;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class ButtonComponent extends FormComponent {

    public ButtonComponent(String componentLabel, String buttonLabel, EventHandler<ActionEvent> consumer) {
        this(componentLabel, buttonLabel, consumer, null);
    }

    public ButtonComponent(String componentLabel, String buttonLabel, EventHandler<ActionEvent> consumer, String description) {
        super(componentLabel, new Button(buttonLabel), description);

        Button button = getButton();
        button.setOnAction(consumer);
        button.getStyleClass().add("form-button");
    }

    public Button getButton() {
        return (Button) getCTA();
    }
}
