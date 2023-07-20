package io.github.clooooud.barbcd.gui.scenes.admin;

import io.github.clooooud.barbcd.BarBCD;

import java.util.function.Function;

public enum AdminScene {
    MAIN_PAGE("Accueil", false, MainAdminScene::new, "home.png"), // home
    OEUVRE_PAGE("Oeuvres", true, MainAdminScene::new, "oeuvre.png"), // book
    CATEGORIE_PAGE("Catégories", true, MainAdminScene::new, "folder.png"), // folder
    EDITOR_PAGE("Editeurs", true, MainAdminScene::new, "edit.png"), // edit
    MAGAZINE_PAGE("Périodiques", true, MainAdminScene::new, "book-open.png"), // book open
    BORROWING_PAGE("Emprunts", false, MainAdminScene::new, "shopping-bag.png"), // shopping bag
    USER_PAGE("Utilisateurs", true, MainAdminScene::new, "users.png"), // user
    STUDENT_PAGE("Étudiants", false, MainAdminScene::new, "users.png"), // user
    SETTING_PAGE("Paramètres", true, SettingsAdminScene::new, "settings.png"); // setting

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
