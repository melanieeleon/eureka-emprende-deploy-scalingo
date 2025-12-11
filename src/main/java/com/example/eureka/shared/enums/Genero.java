package com.example.eureka.shared.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Genero {
    Masculino,
    Femenino;

    @JsonCreator
    public static Genero fromString(String value) {
        if (value == null) {
            return null;
        }
        for (Genero genero : Genero.values()) {
            if (genero.name().equalsIgnoreCase(value)) {
                return genero;
            }
        }
        throw new IllegalArgumentException("Genero inválido: " + value);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
