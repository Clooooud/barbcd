package io.github.clooooud.barbcd.gui.scenes.admin.category;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.model.document.Category;
import io.github.clooooud.barbcd.gui.element.components.FieldComponent;
import io.github.clooooud.barbcd.gui.element.FormBox;
import io.github.clooooud.barbcd.gui.element.components.SearchFieldComponent;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class NewCategoryScene extends RootAdminScene {

    private FormBox formBox;

    public NewCategoryScene(BarBCD app) {
        super(app);
    }

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);

        ObservableList<Category> categories = FXCollections.observableArrayList(this.getLibrary().getDocuments(SaveableType.CATEGORY).stream().map(document -> (Category) document).toList());

        this.formBox = new FormBox.Builder("Nouvelle catégorie")
                .addComponent("name", new FieldComponent("Nom"))
                .addComponent("parent", new SearchFieldComponent<>("Catégorie parente (Non-obligatoire)", categories, "Cet option permet de définir la nouvelle catégorie comme une sous-catégorie d'une autre catégorie. Elle n'est pas obligatoire"))
                .addButton("Sauvegarder", event -> consumeForm())
                .addButton("Annuler", event -> this.getApp().getStageWrapper().setContent(new CategoriesScene(this.getApp())))
                .build();

        vBox.getChildren().add(this.formBox);
    }

    private void consumeForm() {
        String name = ((FieldComponent) this.formBox.getComponent("name")).getField().getText();
        Category parent = ((SearchFieldComponent<Category>) this.formBox.getComponent("parent")).getSelected();

        if (name.isBlank()) {
            GuiUtil.alertError("Le nom de la catégorie ne peut pas être vide");
            return;
        }

        System.out.println(parent);

        Category category = this.getLibrary().createCategory(name, parent);
        this.getApp().getStageWrapper().setContent(new CategoryScene(this.getApp(), category));
        SaveRunnable.create(this.getApp()).run();
    }
}
