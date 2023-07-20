package io.github.clooooud.barbcd.gui.element;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class FormComponent extends VBox {

    private Label componentLabel;
    private Node ctaNode;
    private Label descriptionLabel;

    public FormComponent(String componentLabel, Node ctaNode) {
        this(componentLabel, ctaNode, null);
    }

    public FormComponent(String componentLabel, Node ctaNode, String description) {
        super();

        this.componentLabel = new Label(componentLabel);
        this.ctaNode = ctaNode;
        if (description != null) {
            this.descriptionLabel = new Label(description);
        }

        initComponent();
    }

    public Node getCTA() {
        return ctaNode;
    }

    private void initComponent() {
        this.setSpacing(3);

        componentLabel.getStyleClass().add("form-component-label");
        this.getChildren().add(componentLabel);

        if (descriptionLabel != null) {
            descriptionLabel.getStyleClass().add("font-component-description");
            descriptionLabel.setWrapText(true);
            this.getChildren().add(descriptionLabel);
        }

        this.getChildren().add(ctaNode);
    }
}
