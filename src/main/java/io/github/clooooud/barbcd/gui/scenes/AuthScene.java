package io.github.clooooud.barbcd.gui.scenes;

import io.github.clooooud.barbcd.BarBCD;
import javafx.scene.control.Button;

import java.util.Collections;
import java.util.List;

public class AuthScene extends FormScene {

    public AuthScene(BarBCD app) {
        super(app);
    }

    @Override
    protected void initButton(String buttonName, Button button) {
        // There is only one button, no more needed
        // TODO: connection
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
        return Collections.singletonList("Mot de passe");
    }

    @Override
    protected List<String> getButtonNames() {
        return Collections.singletonList("Se connecter");
    }
}
