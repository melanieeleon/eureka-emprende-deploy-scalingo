package com.example.eureka.shared.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoEvento {
    presencial,
    virtual;

    @JsonCreator
    public static TipoEvento fromString(String value) {
        if (value == null) {
            return null;
        }
        for (TipoEvento tipo : TipoEvento.values()) {
            if (tipo.name().equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de evento inválido: " + value);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}