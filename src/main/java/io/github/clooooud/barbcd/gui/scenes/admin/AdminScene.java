package io.github.clooooud.barbcd.gui.scenes.admin;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.gui.scenes.RootAdminScene;

import java.util.function.Function;

public enum AdminScene {
    MAIN_PAGE("Accueil", false, MainAdminScene::new),
    OEUVRE_PAGE("Oeuvre", true, MainAdminScene::new),
    CATEGORIE_PAGE("Catégorie", true, MainAdminScene::new),
    EDITOR_PAGE("Editeur", true, MainAdminScene::new),
    MAGAZINE_PAGE("Périodique", true, MainAdminScene::new),
    BORROWING_PAGE("Emprunt", false, MainAdminScene::new),
    USER_PAGE("Utilisateur", true, MainAdminScene::new),
    SETTING_PAGE("Paramètres", true, MainAdminScene::new);

    private final String sceneName;
    private final boolean adminOnly;
    private final Function<BarBCD, RootAdminScene> scene;

    AdminScene(String sceneName, boolean adminOnly, Function<BarBCD, RootAdminScene> scene) {
        this.sceneName = sceneName;
        this.adminOnly = adminOnly;
        this.scene = scene;
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
