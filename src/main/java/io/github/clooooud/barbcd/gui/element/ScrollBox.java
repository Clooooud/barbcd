package io.github.clooooud.barbcd.gui.element;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ScrollBox extends ScrollPane {

    public ScrollBox(Region parentRegion, Region content, boolean vgrow) {
        super(content);
        this.setFitToWidth(true);
        
        if (vgrow) {
            VBox.setVgrow(this, Priority.ALWAYS);
            parentRegion.parentProperty().addListener(((observable, oldVal, newVal) -> {
                if (newVal == null) {
                    return;
                }

                this.prefViewportHeightProperty().bind(Bindings.min(
                        newVal.getScene().heightProperty().subtract(100).subtract(parentRegion.getPadding().getTop() + parentRegion.getPadding().getBottom()),
                        content.heightProperty()
                ));
            }));
        }
    }
}
