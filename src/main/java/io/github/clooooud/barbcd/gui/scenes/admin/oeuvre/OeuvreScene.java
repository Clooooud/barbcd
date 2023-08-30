package io.github.clooooud.barbcd.gui.scenes.admin.oeuvre;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.model.document.Oeuvre;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import javafx.scene.layout.VBox;

public class OeuvreScene extends RootAdminScene {

    private final Oeuvre oeuvre;

    public OeuvreScene(BarBCD app, Oeuvre oeuvre) {
        super(app);
        this.oeuvre = oeuvre;
    }

    @Override
    public void initAdminContent(VBox vBox) {

    }
}
