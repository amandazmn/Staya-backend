package com.staya.api.exception;

/** Lançada quando a operação viola uma regra de unicidade ou integridade (HTTP 409). */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
