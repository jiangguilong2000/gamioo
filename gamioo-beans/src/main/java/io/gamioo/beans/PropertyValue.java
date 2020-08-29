package io.gamioo.beans;

public class PropertyValue {

    private final String name;

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    private final Object value;

    public PropertyValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }
}