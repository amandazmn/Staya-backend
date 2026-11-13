package com.staya.api.service;

import com.staya.api.dto.QuartoRequestDTO;
import com.staya.api.dto.QuartoResponseDTO;
import com.staya.api.exception.ConflitoException;
import com.staya.api.exception.RecursoNaoEncontradoException;
import com.staya.api.mapper.QuartoMapper;
import com.staya.api.model.Quarto;
import com.staya.api.model.StatusQuarto;
import com.staya.api.repository.QuartoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuartoService {

    private final QuartoRepository quartoRepository;
    private final TipoQuartoService tipoQuartoService;

    public QuartoService(QuartoRepository quartoRepository, TipoQuartoService tipoQuartoService) {
        this.quartoRepository = quartoRepository;
        this.tipoQuartoService = tipoQuartoService;
    }

    @Transactional(readOnly = true)
    public List<QuartoResponseDTO> listar(StatusQuarto status, Long tipoQuartoId, Integer andar) {
        return quartoRepository.buscar(status, tipoQuartoId, andar).stream().map(QuartoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public QuartoResponseDTO buscar(Long id) {
        return QuartoMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public QuartoResponseDTO criar(QuartoRequestDTO dto) {
        validarNumeroUnico(dto.getNumero().trim(), null);
        Quarto quarto = new Quarto();
        preencher(dto, quarto);
        return QuartoMapper.toResponse(quartoRepository.save(quarto));
    }

    @Transactional
    public QuartoResponseDTO atualizar(Long id, QuartoRequestDTO dto) {
        Quarto quarto = buscarEntidade(id);
        validarNumeroUnico(dto.getNumero().trim(), id);
        preencher(dto, quarto);
        return QuartoMapper.toResponse(quarto);
    }

    @Transactional
    public QuartoResponseDTO alterarStatus(Long id, StatusQuarto novoStatus) {
        Quarto quarto = buscarEntidade(id);
        quarto.setStatus(novoStatus);
        return QuartoMapper.toResponse(quarto);
    }

    @Transactional
    public void excluir(Long id) {
        Quarto quarto = buscarEntidade(id);
        if (quarto.getStatus() == StatusQuarto.OCUPADO) {
            throw new ConflitoException("Quarto ocupado não pode ser excluído");
        }
        quartoRepository.delete(quarto);
    }

    private void preencher(QuartoRequestDTO dto, Quarto quarto) {
        QuartoMapper.copiar(dto, quarto);
        quarto.setTipoQuarto(tipoQuartoService.buscarEntidade(dto.getTipoQuartoId()));
    }

    private void validarNumeroUnico(String numero, Long idAtual) {
        boolean existe = idAtual == null
                ? quartoRepository.existsByNumero(numero)
                : quartoRepository.existsByNumeroAndIdNot(numero, idAtual);
        if (existe) {
            throw new ConflitoException("Já existe um quarto com o número " + numero);
        }
    }

    private Quarto buscarEntidade(Long id) {
        return quartoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Quarto", id));
    }
}
