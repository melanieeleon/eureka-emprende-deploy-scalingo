package com.example.eureka.shared.enums;

public enum EstadoArticulo {
    BORRADOR("borrador"),
    PUBLICADO("publicado"),
    ARCHIVADO("archivado");

    private final String valor;

    EstadoArticulo(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoArticulo fromValor(String valor) {
        for (EstadoArticulo estado : EstadoArticulo.values()) {
            if (estado.valor.equalsIgnoreCase(valor)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de artículo no válido: " + valor);
    }
}