package io.github.clooooud.barbcd.gui.scenes.admin;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.gui.scenes.admin.borrowing.BorrowingsScene;
import io.github.clooooud.barbcd.gui.scenes.admin.category.CategoriesScene;
import io.github.clooooud.barbcd.gui.scenes.admin.editor.EditorsScene;
import io.github.clooooud.barbcd.gui.scenes.admin.oeuvre.OeuvresScene;
import io.github.clooooud.barbcd.gui.scenes.admin.student.ClassesScene;
import io.github.clooooud.barbcd.gui.scenes.admin.user.UsersScene;

import java.util.function.Function;

public enum AdminScene {
    MAIN_PAGE("Accueil", false, MainAdminScene::new, "home.png"),
    OEUVRE_PAGE("Oeuvres", true, OeuvresScene::new, "oeuvre.png"),
    CATEGORY_PAGE("Catégories", true, CategoriesScene::new, "folder.png"),
    EDITOR_PAGE("Editeurs", true, EditorsScene::new, "edit.png"),
    MAGAZINE_PAGE("Périodiques", true, MainAdminScene::new, "book-open.png"),
    BORROWING_PAGE("Emprunts", false, BorrowingsScene::new, "shopping-bag.png"),
    USER_PAGE("Utilisateurs", true, UsersScene::new, "users.png"),
    CLASSES_PAGE("Classes", false, ClassesScene::new, "list.png"),
    SETTING_PAGE("Paramètres", true, SettingsScene::new, "settings.png");

    private final String sceneName;
    private final boolean adminOnly;
    private final Function<BarBCD, RootAdminScene> scene;
    private final String iconName;

    AdminScene(String sceneName, boolean adminOnly, Function<BarBCD, RootAdminScene> scene, String iconName) {
        this.sceneName = sceneName;
        this.adminOnly = adminOnly;
        this.scene = scene;
        this.iconName = iconName;
    }

    public String getIconName() {
        return "assets/" + iconName;
    }

    public String getSceneName() {
        return sceneName;
    }

    public boolean isAdminOnly() {
        return adminOnly;
    }

    public RootAdminScene getScene(BarBCD app) {
        return scene.apply(app);
    }
}
