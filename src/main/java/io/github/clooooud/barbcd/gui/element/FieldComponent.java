package io.github.clooooud.barbcd.gui.element;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class FieldComponent extends FormComponent {

    public FieldComponent(String labelText) {
        this(labelText, null);
    }

    public FieldComponent(String labelText, String description) {
        this(labelText, description, false);
    }

    public FieldComponent(String labelText, String description, boolean password) {
        super(labelText, password ? new PasswordField() : new TextField(), description);
    }

    public FieldComponent(String labelText, boolean password) {
        this(labelText, null, password);
    }

    public TextField getField() {
        return (TextField) getCTA();
    }
}
