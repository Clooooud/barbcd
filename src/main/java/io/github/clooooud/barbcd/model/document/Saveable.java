package io.github.clooooud.barbcd.model.document;

import java.util.List;

public interface Saveable {

    default String getSheetName() {
        return this.getClass().getSimpleName() + "s";
    }

    void markAsUpdated();

    int getId();

    List<Object> getHeaders();

    List<Object> getValues();

    boolean needsSave();
}
