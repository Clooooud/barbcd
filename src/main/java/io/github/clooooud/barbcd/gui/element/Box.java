package io.github.clooooud.barbcd.gui.element;

import javafx.beans.binding.Bindings;
import javafx.scene.layout.VBox;

public class Box extends VBox {

    protected final VBox vBox = new VBox();
    
    public Box() {
        ScrollBox scrollBox = new ScrollBox(this, vBox, true);
        this.getChildren().add(scrollBox);
        this.getStyleClass().add("box");
        vBox.getStyleClass().add("form-box");

        parentProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null) {
                maxWidthProperty().bind(Bindings.min(600, newVal.getScene().widthProperty().subtract(300)));
            }
        });
    }
}
