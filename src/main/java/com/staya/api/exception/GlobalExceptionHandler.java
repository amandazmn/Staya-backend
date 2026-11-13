package com.staya.api.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converte exceções em respostas no padrão RFC 9457 (application/problem+json).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail handleNaoEncontrado(RecursoNaoEncontradoException ex) {
        return problem(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage());
    }

    @ExceptionHandler(ConflitoException.class)
    public ProblemDetail handleConflito(ConflitoException ex) {
        return problem(HttpStatus.CONFLICT, "Conflito", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleIntegridade(DataIntegrityViolationException ex) {
        return problem(HttpStatus.CONFLICT, "Conflito",
                "A operação viola uma restrição de integridade dos dados");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleCorpoInvalido(HttpMessageNotReadableException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Requisição inválida",
                "O corpo da requisição está ausente ou mal formatado");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleParametroInvalido(MethodArgumentTypeMismatchException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Requisição inválida",
                "Valor inválido para o parâmetro '" + ex.getName() + "': " + ex.getValue());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new LinkedHashMap<>();
        for (ObjectError erro : ex.getBindingResult().getAllErrors()) {
            String campo = erro instanceof FieldError fe ? fe.getField() : erro.getObjectName();
            erros.putIfAbsent(campo, erro.getDefaultMessage());
        }
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Dados inválidos",
                "Um ou mais campos estão inválidos");
        pd.setProperty("erros", erros);
        return pd;
    }

    private ProblemDetail problem(HttpStatus status, String titulo, String detalhe) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detalhe);
        pd.setTitle(titulo);
        return pd;
    }
}
