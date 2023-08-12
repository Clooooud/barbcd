package io.github.clooooud.barbcd.gui.scenes.admin.user;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.auth.User;
import io.github.clooooud.barbcd.data.model.classes.Class;
import io.github.clooooud.barbcd.gui.scenes.admin.ListAdminScene;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;

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

    protected void delete(User user) {
        this.getLibrary().removeDocument(user);
        user.getResponsibilities(this.getLibrary()).forEach(responsibility -> this.getLibrary().removeDocument(responsibility));
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
    protected RootAdminScene getRefreshedScene() {
        return new UsersScene(this.getApp());
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
        return super.canDeleteObject(object) && !object.isAdmin();
    }

    @Override
    protected String getDeleteObjectMessage() {
        return "Voulez-vous vraiment supprimer cet utilisateur ?";
    }

    @Override
    protected String getDeleteObjectsMessage() {
        return "Voulez-vous vraiment supprimer ces utilisateurs ?";
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
