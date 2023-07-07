package io.github.clooooud.barbcd.gui.scenes;

import io.github.clooooud.barbcd.BarBCD;
import javafx.scene.layout.VBox;

import java.util.List;

public class StartScene extends FormScene {

    public StartScene(BarBCD app) {
        super(app);
    }

    @Override
    public void initContent(VBox vBox) {
        super.initContent(vBox);

    }

    @Override
    protected String getTitle() {
        return "Bienvenue dans BarBCD !";
    }

    @Override
    protected String getDescription() {
        return "Pour utiliser cette application, vous devez y entrer des données importantes. " +
                "Nous utilisons Google Drive comme base de donnée et par conséquent certains paramétrages sont nécessaires. " +
                "Veuillez suivre le tutoriel si besoin.";
    }

    @Override
    protected List<String> getFieldNames() {
        return List.of("Mot de passe Admin", "Compte de service Google", "Clé API Google");
    }

    @Override
    protected List<String> getButtonNames() {
        return List.of("Tutoriel", "Suivant");
    }


}
