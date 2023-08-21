package io.github.clooooud.barbcd.gui.element;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.controlsfx.control.SearchableComboBox;

import java.util.function.Function;

public class SearchFieldComponent<E> extends FormComponent {

    public SearchFieldComponent(String componentLabel, ObservableList<E> list, String description) {
        super(componentLabel, new SearchField<>(list), description);
    }

    public SearchFieldComponent(String componentLabel, ObservableList<E> list) {
        this(componentLabel, list, null);
    }

    public E getSelected() {
        ListCell<E> buttonCell = ((SearchField<E>) this.getCTA()).getButtonCell();

        if (buttonCell == null) {
            return null;
        }

        return buttonCell.getItem();
    }

    private static class SearchField<E> extends SearchableComboBox<E> {

        public SearchField(ObservableList<E> list) {
            this.setItems(list);
            this.setCellFactory(new Callback<>() {
                @Override
                public ListCell<E> call(ListView<E> p) {
                    return new ListCell<>() {
                        @Override
                        protected void updateItem(E item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setText("");
                            } else {
                                setText(item.toString());
                            }
                        }
                    };
                }
            });


        }
    }

}
