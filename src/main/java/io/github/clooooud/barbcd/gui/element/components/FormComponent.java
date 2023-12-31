package io.github.clooooud.barbcd.gui.element.components;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FormComponent extends VBox {

    private final Label componentLabel;
    private final Node ctaNode;
    private Label descriptionLabel;

    public FormComponent(String componentLabel, Node ctaNode) {
        this(componentLabel, ctaNode, null);
    }

    public FormComponent(String componentLabel, Node ctaNode, String description) {
        super();

        this.componentLabel = !componentLabel.isBlank() ? new Label(componentLabel) : null;
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

        if (componentLabel != null) {
            componentLabel.getStyleClass().add("form-component-label");
            this.getChildren().add(componentLabel);
        }

        if (descriptionLabel != null) {
            descriptionLabel.getStyleClass().add("form-component-description");
            descriptionLabel.setWrapText(true);
            this.getChildren().add(descriptionLabel);
        }

        this.getChildren().add(ctaNode);
    }
}
