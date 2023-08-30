package io.github.clooooud.barbcd.gui.scenes.admin.category;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.model.document.Category;
import io.github.clooooud.barbcd.data.model.document.Oeuvre;
import io.github.clooooud.barbcd.gui.scenes.admin.ListAdminScene;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;

import java.util.List;

public class CategoriesScene extends ListAdminScene<Category> {

    public CategoriesScene(BarBCD app) {
        super(app);
    }

    @Override
    protected List<Category> getObjects() {
        return this.getLibrary().getDocuments(SaveableType.CATEGORY).stream()
                .map(document -> (Category) document)
                .sorted().toList();
    }

    @Override
    protected void delete(Category category) {
        this.getLibrary().removeDocument(category);
        this.getLibrary().getViewableDocuments().stream().filter(viewableDocument -> viewableDocument.getCategory().equals(category)).forEach(viewableDocument -> {
            if (viewableDocument instanceof Oeuvre) {
                ((Oeuvre) viewableDocument).setCategory(null);
            }
        });
    }

    @Override
    protected String getTitle() {
        return "Catégories";
    }

    @Override
    protected String getFilterString(Category category) {
        return category.getName();
    }

    @Override
    protected RootAdminScene getRefreshedScene() {
        return new CategoriesScene(this.getApp());
    }

    @Override
    protected RootAdminScene getNewObjectScene() {
        return new NewCategoryScene(this.getApp());
    }

    @Override
    protected RootAdminScene getObjectScene(Category category) {
        return new CategoryScene(this.getApp(), category);
    }

    @Override
    protected String getListObjectName(Category category) {
        return category.getName();
    }

    @Override
    protected String getListObjectDesc(Category category) {
        long count = this.getLibrary().getViewableDocuments().stream()
                .filter(viewableDocument -> viewableDocument.getCategory().equals(category))
                .count();

        Category parent = category.getParent(this.getLibrary());
        return (parent == null ? "" : "Sous-catégorie de " + parent.getName() + " - ") +  count + " document" + (count > 1 ? "s" : "");
    }

    @Override
    protected String getDeleteObjectMessage() {
        return "Êtes-vous sûr de vouloir supprimer cette catégorie ? Toutes les oeuvres de cette catégorie perdront leur catégorie.";
    }

    @Override
    protected String getDeleteObjectsMessage() {
        return "Êtes-vous sûr de vouloir supprimer ces catégories ? Toutes les oeuvres de ces catégories perdront leur catégorie.";
    }
}
