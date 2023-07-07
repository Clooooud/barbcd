package io.github.clooooud.barbcd.gui.scenes;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.model.Library;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AuthScene extends RootScene {

    private Library library;

    public AuthScene(BarBCD app) {
        super(app);
        this.library = app.getLibrary();
    }

    @Override
    public void initContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(new Label("En cours"));
    }
}
