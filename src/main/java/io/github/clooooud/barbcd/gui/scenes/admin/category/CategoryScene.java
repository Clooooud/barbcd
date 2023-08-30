package io.github.clooooud.barbcd.gui.scenes.admin.category;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.model.document.Category;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import javafx.scene.layout.VBox;

public class CategoryScene extends RootAdminScene {

    private final Category category;

    public CategoryScene(BarBCD app, Category category) {
        super(app);
        this.category = category;
    }

    @Override
    public void initAdminContent(VBox vBox) {

    }
}
