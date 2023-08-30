package io.github.clooooud.barbcd.gui.scenes.admin;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.gui.scenes.RootScene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static io.github.clooooud.barbcd.gui.StageWrapper.getResource;

public abstract class RootAdminScene extends RootScene {

    public RootAdminScene(BarBCD app) {
        super(app);
    }

    @Override
    public void initContent(VBox vBox) {
        HBox hBox = new HBox();
        vBox.getChildren().add(hBox);
        VBox.setVgrow(hBox, Priority.ALWAYS);

        VBox sideBox = new VBox();
        sideBox.setMaxWidth(200);
        sideBox.setPrefWidth(200);
        sideBox.setMinWidth(200);
        sideBox.setId("sidebox");

        fillSideBox(sideBox);

        VBox contentVBox = new VBox();

        hBox.getChildren().addAll(sideBox, contentVBox);

        HBox.setHgrow(contentVBox, Priority.ALWAYS);

        initAdminContent(contentVBox);
    }

    private void fillSideBox(VBox sideBox) {
        boolean isAdmin = this.getApp().getLibrary().getUser().isAdmin();

        AdminScene[] values = AdminScene.values();
        for (int i = 0; i < values.length; i++) {
            AdminScene scene = values[i];
            if (scene.isAdminOnly() && !isAdmin) {
                continue;
            }

            HBox sideButton = generateSideButton(scene);
            sideButton.prefHeightProperty().bind(
                    this.getApp().getStageWrapper().getStage().minHeightProperty()
                    .subtract(100)
                    .divide(values.length)
            );

            if (scene != AdminScene.MAIN_PAGE && scene.getScene(this.getApp()).getClass().equals(AdminScene.MAIN_PAGE.getScene(this.getApp()).getClass())) {
                sideButton.setStyle("-fx-background-color: red;");
            }

            if (i < values.length-1) {
                sideButton.getStyleClass().add("sidebox-button-notlast");
            }

            sideBox.getChildren().add(sideButton);
        }
    }

    private HBox generateSideButton(AdminScene scene) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("sidebox-button");
        hBox.setSpacing(5);

        ImageView imageView = new ImageView(new Image(getResource(scene.getIconName())));
        imageView.setSmooth(false);

        Label label = new Label(scene.getSceneName());
        label.setFont(Font.font(null, FontWeight.BOLD, 16));
        label.getStyleClass().add("sidebox-button-label");

        hBox.getChildren().addAll(imageView, label);

        hBox.setOnMouseClicked(mouseEvent -> this.getApp().getStageWrapper().setContent(scene.getScene(this.getApp())));

        return hBox;
    }

    public abstract void initAdminContent(VBox vBox);


}
