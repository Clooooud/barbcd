package io.github.clooooud.barbcd.gui.element;

import io.github.clooooud.barbcd.gui.StageWrapper;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.textfield.CustomTextField;

import java.util.stream.Stream;

public class ValidableFieldComponent extends FormComponent {

    public ValidableFieldComponent(String componentLabel, Validable validable) {
        this(componentLabel, validable, null);
    }

    public ValidableFieldComponent(String componentLabel, Validable validable, String description) {
        super(componentLabel, new CustomTextField(), description);

        CustomTextField field = (CustomTextField) getCTA();

        ImageView wrongISBN = new ImageView(new Image(StageWrapper.getResource("assets/x.png")));
        ImageView goodISBN = new ImageView(new Image(StageWrapper.getResource("assets/check.png")));

        Stream.of(wrongISBN, goodISBN).forEach(imageView -> {
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
        });

        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (validable.isValid(newValue)) {
                field.setRight(goodISBN);
            } else if (newValue.isBlank()) {
                field.setRight(null);
            } else {
                field.setRight(wrongISBN);
            }
        });
    }

    public TextField getField() {
        return (TextField) getCTA();
    }

    public interface Validable {
        boolean isValid(String string);
    }
}
