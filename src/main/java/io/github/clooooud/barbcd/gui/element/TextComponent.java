package io.github.clooooud.barbcd.gui.element;

import javafx.scene.text.Text;

public class TextComponent extends FormComponent {

    public TextComponent(String componentLabel, String content) {
        super(componentLabel, new Text(content));
        getTextNode().getStyleClass().add("text-component");
    }

    public Text getTextNode() {
        return (Text) this.getCTA();
    }

    public void setText(String text) {
        this.getTextNode().setText(text);
    }

}
