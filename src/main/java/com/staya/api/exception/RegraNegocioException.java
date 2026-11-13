package com.staya.api.exception;

/** Dados bem formados, mas que violam uma regra do negócio (HTTP 422). */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
