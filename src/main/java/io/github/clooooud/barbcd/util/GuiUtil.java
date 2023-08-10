package io.github.clooooud.barbcd.util;

import io.github.clooooud.barbcd.gui.StageWrapper;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

public class GuiUtil {

    public static boolean isNodeClicked(double clickX, double clickY, Region node) {
        return (clickX >= (node.getLayoutX() + node.getTranslateX()) && clickX <= (node.getLayoutX() + node.getTranslateX()) + node.getWidth())
                && (clickY >= (node.getLayoutY() + node.getTranslateY()) && clickY <= (node.getLayoutY() + node.getTranslateY()) + node.getHeight());
    }

    public static Alert wrapAlert(Alert alert) {
        alert.getDialogPane().getStylesheets().add(StageWrapper.getResourceUrl("style.css"));
        return alert;
    }
}
