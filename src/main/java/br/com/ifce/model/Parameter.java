package br.com.ifce.model;

import java.util.Arrays;

public enum Parameter {
    TEMPERATURE("Temperature"),
    HUMIDITY("Humidity"),
    SPEED("Speed");

    public final String text;

    Parameter(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static String[] textValues() {
        return new String[]{TEMPERATURE.text, HUMIDITY.text, SPEED.text};
    }

    public static Parameter fromString(String text) {
        return Arrays.stream(Parameter.values())
            .filter(it -> it.text.equalsIgnoreCase(text))
            .findFirst()
            .orElse(null);
    }

    @Override
    public String toString() {
        return text;
    }
}
