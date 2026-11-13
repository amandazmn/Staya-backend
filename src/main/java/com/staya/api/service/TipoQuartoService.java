package com.staya.api.service;

import com.staya.api.dto.TipoQuartoRequestDTO;
import com.staya.api.dto.TipoQuartoResponseDTO;
import com.staya.api.exception.ConflitoException;
import com.staya.api.exception.RecursoNaoEncontradoException;
import com.staya.api.mapper.TipoQuartoMapper;
import com.staya.api.model.TipoQuarto;
import com.staya.api.repository.QuartoRepository;
import com.staya.api.repository.TipoQuartoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TipoQuartoService {

    private final TipoQuartoRepository tipoQuartoRepository;
    private final QuartoRepository quartoRepository;

    public TipoQuartoService(TipoQuartoRepository tipoQuartoRepository, QuartoRepository quartoRepository) {
        this.tipoQuartoRepository = tipoQuartoRepository;
        this.quartoRepository = quartoRepository;
    }

    @Transactional(readOnly = true)
    public List<TipoQuartoResponseDTO> listar() {
        return tipoQuartoRepository.findAll(Sort.by("nome")).stream().map(TipoQuartoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TipoQuartoResponseDTO buscar(Long id) {
        return TipoQuartoMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public TipoQuartoResponseDTO criar(TipoQuartoRequestDTO dto) {
        validarNomeUnico(dto.getNome().trim(), null);
        TipoQuarto tipo = new TipoQuarto();
        TipoQuartoMapper.copiar(dto, tipo);
        return TipoQuartoMapper.toResponse(tipoQuartoRepository.save(tipo));
    }

    @Transactional
    public TipoQuartoResponseDTO atualizar(Long id, TipoQuartoRequestDTO dto) {
        TipoQuarto tipo = buscarEntidade(id);
        validarNomeUnico(dto.getNome().trim(), id);
        TipoQuartoMapper.copiar(dto, tipo);
        return TipoQuartoMapper.toResponse(tipo);
    }

    @Transactional
    public void excluir(Long id) {
        TipoQuarto tipo = buscarEntidade(id);
        if (quartoRepository.existsByTipoQuartoId(id)) {
            throw new ConflitoException("Tipo de quarto possui quartos cadastrados e não pode ser excluído");
        }
        tipoQuartoRepository.delete(tipo);
    }

    TipoQuarto buscarEntidade(Long id) {
        return tipoQuartoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de quarto", id));
    }

    private void validarNomeUnico(String nome, Long idAtual) {
        boolean existe = idAtual == null
                ? tipoQuartoRepository.existsByNomeIgnoreCase(nome)
                : tipoQuartoRepository.existsByNomeIgnoreCaseAndIdNot(nome, idAtual);
        if (existe) {
            throw new ConflitoException("Já existe um tipo de quarto com o nome '" + nome + "'");
        }
    }
}
