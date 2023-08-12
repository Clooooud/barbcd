package io.github.clooooud.barbcd.gui.scenes.admin.user;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.auth.User;
import io.github.clooooud.barbcd.data.model.classes.Class;
import io.github.clooooud.barbcd.gui.scenes.admin.ListAdminScene;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.util.GuiUtil;
import javafx.scene.control.Alert;

import java.util.List;

public class UsersScene extends ListAdminScene<User> {

    public UsersScene(BarBCD app) {
        super(app);
    }

    @Override
    protected List<User> getObjects() {
        return this.getLibrary().getUsers().stream()
                .sorted().toList();
    }

    @Override
    protected void massDelete() {
        GuiUtil.wrapAlert(new Alert(
                Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer ces utilisateurs ?"
        )).showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData().isDefaultButton()) {
                getSelectedObjects().forEach(user -> {
                    this.getLibrary().removeDocument(user);
                    user.getResponsibilities(this.getLibrary()).forEach(responsibility -> this.getLibrary().removeDocument(responsibility));
                });
                SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
                this.getApp().getStageWrapper().setContent(new UsersScene(this.getApp()));
            }
        });
    }

    protected void deleteObject(User user) {
        GuiUtil.wrapAlert(new Alert(
                Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer cet utilisateur ?"
        )).showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData().isDefaultButton()) {
                this.getLibrary().removeDocument(user);
                user.getResponsibilities(this.getLibrary()).forEach(responsibility -> this.getLibrary().removeDocument(responsibility));
                SaveRunnable.create(this.getLibrary(), this.getApp().getGSheetApi(), this.getLibrary().getAdminPassword()).run();
                this.getApp().getStageWrapper().setContent(new UsersScene(this.getApp()));
            }
        });
    }

    @Override
    protected String getTitle() {
        return "Utilisateurs";
    }

    @Override
    protected String getFilterPrompt() {
        return "Rechercher un utilisateur";
    }

    @Override
    protected String getFilterString(User object) {
        return object.getLogin();
    }

    @Override
    protected RootAdminScene getNewObjectScene() {
        return new NewUserScene(this.getApp());
    }

    @Override
    protected RootAdminScene getObjectScene(User object) {
        return new UserScene(this.getApp(), object);
    }

    @Override
    protected String getListObjectName(User object) {
        return object.getLogin();
    }

    @Override
    protected boolean canDeleteObject(User object) {
        return !object.isAdmin();
    }

    protected String getListObjectDesc(User object) {
        if (object.isAdmin()) {
            return "Cet utilisateur a accès à toutes les classes.";
        }

        List<String> classesName = object.getOwnedClasses(this.getLibrary())
                .stream()
                .map(Class::getClassName)
                .toList();

        String content;

        if (classesName.isEmpty()) {
            content = "Cet utilisateur n'a aucune classe assignée.";
        } else {
            content = "Classe" + (classesName.size() > 1 ? "s" : "") + " assignée" + (classesName.size() > 1 ? "s" : "") + " : " +
                    String.join(", ", classesName);
        }

        return content;
    }
}
