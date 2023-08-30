package io.github.clooooud.barbcd.gui.element;

public class IntFieldComponent extends FieldComponent {

    public IntFieldComponent(String labelText) {
        this(labelText, null, false);
    }

    public IntFieldComponent(String labelText, String description) {
        this(labelText, description, false);
    }

    public IntFieldComponent(String labelText, String description, boolean password) {
        super(labelText, description, password);
        applyIntFilter();
    }

    public IntFieldComponent(String labelText, boolean password) {
        this(labelText, null, password);
    }

    private void applyIntFilter() {
        this.getField().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                this.getField().setText(newValue.replaceAll("\\D", ""));
            }
        });
    }
}
