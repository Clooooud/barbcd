package io.github.clooooud.barbcd.gui.scenes;

import io.github.clooooud.barbcd.BarBCD;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.HBox;

import java.util.Collections;
import java.util.List;

public class AuthScene extends FormScene {

    public AuthScene(BarBCD app) {
        super(app);
    }

    @Override
    public HBox getHeader() {
        HBox header = super.getHeader();

        getAuthButton().setOnAction((event) -> getFields().forEach(TextInputControl::clear));

        return header;
    }

    @Override
    protected Runnable getButtonAction(String buttonName, Button button) {
        return this::consumeForm;
    }

    private void consumeForm() {
        if (!this.getFields().stream().allMatch(this::validateNonEmptyTextField)) {
            return;
        }

        String login = this.getField("Utilisateur").getText();
        String password = this.getField("Mot de passe").getText();
    }

    @Override
    protected String getTitle() {
        return "Authentification";
    }

    @Override
    protected String getDescription() {
        return null;
    }

    @Override
    protected List<String> getFieldNames() {
        return List.of("Utilisateur", "Mot de passe");
    }

    @Override
    protected List<String> getPasswordFieldNames() {
        return List.of();
    }

    @Override
    protected List<String> getButtonNames() {
        return Collections.singletonList("Se connecter");
    }
}
