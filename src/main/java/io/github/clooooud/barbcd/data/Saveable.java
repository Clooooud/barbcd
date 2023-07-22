package io.github.clooooud.barbcd.data;

import java.util.List;

public interface Saveable extends Comparable<Saveable> {

    @Override
    default int compareTo(Saveable o) {
        return this.getId() - o.getId();
    }

    SaveableType getSaveableType();

    int getId();

    List<Object> getValues();
}
