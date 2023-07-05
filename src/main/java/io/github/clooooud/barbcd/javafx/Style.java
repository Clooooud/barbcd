package io.github.clooooud.barbcd.javafx;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Style {

    public static Style get(String styles) {
        Style style = new Style();
        for (String styling : styles.split(";")) {
            style.set(styling);
        }
        return style;
    }

    private Map<String, String> stylings = new HashMap<>();

    public Style() {}

    public Style set(String style) {
        String[] strings = style.replace(";", "").toLowerCase().split(": ");
        stylings.put(strings[0].strip(), strings[1].strip());
        return this;
    }

    @Override
    public String toString() {
        return stylings.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("; "));
    }
}
