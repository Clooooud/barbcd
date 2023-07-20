package io.github.clooooud.barbcd.gui.scenes.admin;

import io.github.clooooud.barbcd.BarBCD;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class MainAdminScene extends RootAdminScene {

    public MainAdminScene(BarBCD app) {
        super(app);
    }

    @Override
    public void initAdminContent(VBox vBox) {
        vBox.setAlignment(Pos.CENTER);

        VBox labelBox = new VBox();
        labelBox.setSpacing(40);
        labelBox.setAlignment(Pos.CENTER);
        labelBox.maxWidthProperty().bind(Bindings.min(400, vBox.widthProperty().subtract(100)));

        Label welcomeLabel = new Label("Bienvenue sur l'interface de gestion de la BCD");
        welcomeLabel.setFont(Font.font(null, FontWeight.BOLD, 28));
        welcomeLabel.setWrapText(true);
        welcomeLabel.setTextAlignment(TextAlignment.CENTER);

        labelBox.getChildren().add(welcomeLabel);

        Label infoLabel = new Label("Vous pouvez accéder aux différentes pages de gestion grâce aux onglets sur la gauche");
        infoLabel.setFont(Font.font(20));
        infoLabel.setWrapText(true);
        infoLabel.setTextAlignment(TextAlignment.JUSTIFY);

        labelBox.getChildren().add(infoLabel);

        vBox.getChildren().add(labelBox);
    }
}
