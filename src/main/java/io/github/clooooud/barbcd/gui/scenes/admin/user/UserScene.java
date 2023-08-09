package io.github.clooooud.barbcd.gui.scenes.admin.user;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.SaveableType;
import io.github.clooooud.barbcd.data.api.tasks.SaveRunnable;
import io.github.clooooud.barbcd.data.auth.User;
import io.github.clooooud.barbcd.data.model.classes.Class;
import io.github.clooooud.barbcd.data.model.classes.Responsibility;
import io.github.clooooud.barbcd.gui.element.FieldComponent;
import io.github.clooooud.barbcd.gui.element.FormBox;
import io.github.clooooud.barbcd.gui.element.FormComponent;
import io.github.clooooud.barbcd.gui.scenes.admin.RootAdminScene;
import io.github.clooooud.barbcd.util.AESUtil;
import io.github.clooooud.barbcd.util.Sha256Util;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;

import java.util.ArrayList;
import java.util.List;

public class UserScene extends RootAdminScene {

    private final User user;
    private TextField passwordField;

    private final List<Runnable> runnableList = new ArrayList<>();

    public UserScene(BarBCD app, User user) {
        super(app);
        this.user = user;
    }

    private void consumeForm() {
        runnableList.forEach(Runnable::run);

        String password = passwordField.getText().strip();
        if (!password.isBlank()) {
            AESUtil actualAes = new AESUtil(this.getLibrary().getAdminPassword());
            AESUtil newAes = new AESUtil(password);
            if (!user.isAdmin()) {
                this.user.setPassword(actualAes.encryptString(password));
                this.user.setMainPassword(newAes.encryptString(this.getLibrary().getAdminPassword()));
            } else {
                this.user.setPassword(Sha256Util.passToSha256(password));
                String oldCredentials = actualAes.decrypt("pr_credentials.enc");
                newAes.encrypt(oldCredentials, "pr_credentials.enc");

                this.getLibrary().getUsers().stream().filter(user1 -> !user1.isAdmin()).forEach(
                        user1 -> {
                            String decryptedPass = actualAes.decryptString(user1.getPassword());
                            user1.setPassword(newAes.encryptString(decryptedPass));
                            user1.setMainPassword(new AESUtil(decryptedPass).encryptString(password));
                            this.getLibrary().markDocumentAsUpdated(user1);
                        }
                );

                this.getLibrary().setAdminPassword(password);
            }
            this.getLibrary().markDocumentAsUpdated(user);
        }

        SaveRunnable.create(getLibrary(), getApp().getGSheetApi(), getLibrary().getAdminPassword()).run(true);
        this.getApp().getStageWrapper().setContent(new UsersScene(this.getApp()));
    }

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);

        FormBox.Builder builder = new FormBox.Builder(user.getLogin());

        if (!user.isAdmin()) {
            builder.addComponent("Classes", getClassSelector());
        }

        FieldComponent passwordComponent = new FieldComponent("Mot de passe", true);
        this.passwordField = passwordComponent.getField();
        builder.addComponent("password", passwordComponent);

        builder.addButton("Annuler", (event) -> this.getApp().getStageWrapper().setContent(new UsersScene(this.getApp())))
                .addButton("Sauvegarder", (event) -> consumeForm());

        FormBox formBox = builder.build();
        vBox.getChildren().add(formBox);
    }

    private FormComponent getClassSelector() {
        CheckComboBox<Class> classList = new CheckComboBox<>();
        classList.setConverter(new StringConverter<>() {
            @Override
            public String toString(Class aClass) {
                return aClass.getClassName();
            }

            @Override
            public Class fromString(String s) {
                return getLibrary().getDocuments(SaveableType.CLASS)
                        .stream()
                        .map(saveable -> (Class) saveable)
                        .filter(aClass -> aClass.getClassName().equals(s))
                        .findFirst().orElse(null);
            }
        });
        classList.getItems().addAll(this.getLibrary().getDocuments(SaveableType.CLASS).stream()
                .map(saveable -> (Class) saveable)
                .toList());

        for (Class ownedClass : this.user.getOwnedClasses(this.getLibrary())) {
            classList.getCheckModel().check(ownedClass);
        }

        classList.getCheckModel().getCheckedItems().addListener((ListChangeListener<Class>) change -> {
            change.next();
            if (change.wasRemoved()) {
                for (Class aClass : change.getRemoved()) {
                    Responsibility responsibility = getLibrary().getDocuments(SaveableType.RESPONSIBILITY).stream()
                            .map(saveable -> (Responsibility) saveable)
                            .filter(resp -> resp.getUser().equals(user) && resp.getOwnedClass().equals(aClass))
                            .findFirst().orElse(null);

                    if (responsibility == null) {
                        continue;
                    }

                    runnableList.add(() -> getLibrary().removeDocument(responsibility));
                }
            }

            if (change.wasAdded()) {
                for (Class aClass : change.getAddedSubList()) {
                    runnableList.add(() -> getLibrary().addDocument(new Responsibility(
                            getLibrary().getNextDocumentId(SaveableType.RESPONSIBILITY),
                            user,
                            aClass)
                    ));
                }
            }
        });

        return new FormComponent("Classes assignées", classList, "Sélectionnez les classes que vous voulez assigner à cet utilisateur.");
    }
}
