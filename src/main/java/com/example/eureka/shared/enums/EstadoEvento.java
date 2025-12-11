package com.example.eureka.shared.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EstadoEvento {
    programado,
    cancelado,
    terminado;

    @JsonCreator
    public static EstadoEvento fromString(String value) {
        if (value == null) {
            return null;
        }
        for (EstadoEvento estado : EstadoEvento.values()) {
            if (estado.name().equalsIgnoreCase(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de evento inválido: " + value);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}