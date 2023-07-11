package io.github.clooooud.barbcd.data;

import io.github.clooooud.barbcd.data.model.Library;

import java.util.List;

public interface Saveable {

    default boolean needsUpdate(Library library) {
        return library.getDataUpdateList().stream().anyMatch(dataRequest -> dataRequest.saveable().equals(this));
    }

    SaveableType getSaveableType();

    int getId();

    List<Object> getValues();
}
