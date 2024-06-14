package com.distrelec.smartedit.converter;

import java.util.Objects;

public class MapEntry<T, U> {

    private T key;
    private U value;

    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public U getValue() {
        return value;
    }

    public void setValue(U value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapEntry<?, ?> mapEntry = (MapEntry<?, ?>) o;
        return Objects.equals(key, mapEntry.key) &&
                Objects.equals(value, mapEntry.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MapEntry{");
        sb.append("key=").append(key);
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
