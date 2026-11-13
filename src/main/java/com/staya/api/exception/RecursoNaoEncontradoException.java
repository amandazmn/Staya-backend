package com.staya.api.exception;

/** Lançada quando o recurso buscado não existe (HTTP 404). */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " com id " + id + " não encontrado");
    }
}
