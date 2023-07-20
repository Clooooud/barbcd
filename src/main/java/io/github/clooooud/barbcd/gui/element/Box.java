package io.github.clooooud.barbcd.gui.element;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class Box extends VBox {

    protected final VBox vBox = new VBox();
    
    public Box() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setContent(vBox);
        this.getChildren().add(scrollPane);
        this.getStyleClass().add("box");
        vBox.getStyleClass().add("form-box");

        parentProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null) {
                scrollPane.prefViewportHeightProperty().bind(Bindings.min(
                        newVal.getScene().heightProperty().subtract(150),
                        vBox.heightProperty()
                ));

                maxWidthProperty().bind(Bindings.min(600, newVal.getScene().widthProperty().subtract(300)));
            }
        });
    }
}
