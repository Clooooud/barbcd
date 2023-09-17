package io.github.clooooud.barbcd.gui.scenes.admin.category;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.model.document.Category;
import io.github.clooooud.barbcd.gui.element.FormBox;
import io.github.clooooud.barbcd.gui.element.components.DeleteSearchFieldComponent;
import io.github.clooooud.barbcd.gui.element.components.FieldComponent;
import io.github.clooooud.barbcd.gui.element.components.SearchFieldComponent;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.gui.scenes.admin.oeuvre.OeuvresScene;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

import java.util.List;

public class CategoryScene extends RootAdminScene {

    private FormBox formBox;
    private final Category category;

    public CategoryScene(BarBCD app, Category category) {
        super(app);
        this.category = category;
    }

    private List<Category> getBlacklist() {
        return List.of(this.category);
    }

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);

        ObservableList<Category> categories = FXCollections.observableArrayList(this.getLibrary().getDocuments(SaveableType.CATEGORY).stream()
                .map(document -> (Category) document)
                .filter(category -> !this.getBlacklist().contains(category))
                .toList());

        this.formBox = new FormBox.Builder("Modifier une catégorie")
                .addComponent("name", new FieldComponent("Nom"))
                .addComponent("parent", new DeleteSearchFieldComponent<>("Catégorie parente (Non-obligatoire)", categories, "Cet option permet de définir la nouvelle catégorie comme une sous-catégorie d'une autre catégorie. Elle n'est pas obligatoire"))
                .addButton("Sauvegarder", event -> consumeForm())
                .addButton("Annuler", event -> this.getApp().getStageWrapper().setContent(new CategoriesScene(this.getApp())))
                .build();

        vBox.getChildren().add(this.formBox);

        FieldComponent name = (FieldComponent) this.formBox.getComponent("name");
        DeleteSearchFieldComponent<Category> parent = (DeleteSearchFieldComponent<Category>) this.formBox.getComponent("parent");

        name.getField().setText(this.category.getName());
        Category categoryParent = this.category.getParent(this.getLibrary());
        if (categoryParent != null) {
            parent.getSearchField().getSelectionModel().select(categoryParent);
        }
    }

    protected void consumeForm() {
        String name = ((FieldComponent) this.formBox.getComponent("name")).getField().getText();
        Category parent = ((DeleteSearchFieldComponent<Category>) this.formBox.getComponent("parent")).getSelected();

        if (name.isBlank()) {
            GuiUtil.alertError("Le nom de la catégorie ne peut pas être vide");
            return;
        }

        boolean needUpdate = false;

        if (!name.equals(this.category.getName())) {
            this.category.setName(name);
            needUpdate = true;
        }

        if (parent != this.category.getParent(this.getLibrary())) {
            this.category.setParentId(parent == null ? -1 : parent.getId());
            needUpdate = true;
        }

        if (needUpdate) {
            this.getLibrary().markDocumentAsUpdated(this.category);
            this.getApp().getStageWrapper().setContent(new CategoriesScene(this.getApp()));
            SaveRunnable.create(this.getApp()).run();
        }
    }
}
