package io.github.clooooud.barbcd.gui.element.components;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class IntSpinnerComponent extends FormComponent {

    public IntSpinnerComponent(String componentLabel) {
        this(componentLabel, null);
    }

    public IntSpinnerComponent(String componentLabel, String description) {
        super(componentLabel, new Spinner<Integer>(), description);
        Spinner<Integer> spinner = getSpinner();
        spinner.setEditable(true);
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 1, 1));
    }

    public Spinner<Integer> getSpinner() {
        return (Spinner<Integer>) getCTA();
    }
}
