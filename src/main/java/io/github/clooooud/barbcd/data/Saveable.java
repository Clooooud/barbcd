package io.github.clooooud.barbcd.data;

import io.github.clooooud.barbcd.data.model.Library;

import java.util.List;

public interface Saveable extends Comparable<Saveable> {

    default boolean needsUpdate(Library library) {
        return library.getDataUpdateList().values().stream().anyMatch(saveables -> saveables.contains(this));
    }

    @Override
    default int compareTo(Saveable o) {
        return this.getId() - o.getId();
    }

    SaveableType getSaveableType();

    int getId();

    List<Object> getValues();
}
