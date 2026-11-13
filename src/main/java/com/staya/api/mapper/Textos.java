package com.staya.api.mapper;

public final class Textos {

    private Textos() {}

    /** Remove espaços nas pontas e converte texto vazio em null (evita colisão em colunas UNIQUE). */
    public static String limpar(String valor) {
        if (valor == null) {
            return null;
        }
        String limpo = valor.trim();
        return limpo.isEmpty() ? null : limpo;
    }
}
