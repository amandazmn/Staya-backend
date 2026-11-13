package com.staya.api.model;

/** Situação operacional de um quarto. */
public enum StatusQuarto {
    /** Limpo e livre para receber hóspedes. */
    DISPONIVEL,
    /** Com hóspede hospedado. */
    OCUPADO,
    /** Aguardando ou em processo de limpeza (governança). */
    LIMPEZA,
    /** Em manutenção; não pode ser vendido. */
    MANUTENCAO,
    /** Bloqueado administrativamente (uso interno, reforma etc.). */
    BLOQUEADO
}
