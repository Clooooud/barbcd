package io.github.clooooud.barbcd.gui.element;

import io.github.clooooud.barbcd.gui.StageWrapper;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public abstract class Popup extends Stage {

    public Popup(String name) {
        super();
        this.setTitle(name);
        this.setScene(getPopupContent());
        this.getScene().getStylesheets().add(StageWrapper.getResourceUrl("style.css"));
        Region root = (Region) getPopupContent().getRoot();
        root.setId("page-content");
    }

    public abstract Scene getPopupContent();
}
