package com.staya.api.service;

import com.staya.api.dto.ProdutoRequestDTO;
import com.staya.api.dto.ProdutoResponseDTO;
import com.staya.api.exception.ConflitoException;
import com.staya.api.exception.RecursoNaoEncontradoException;
import com.staya.api.mapper.ProdutoMapper;
import com.staya.api.model.CategoriaProduto;
import com.staya.api.model.Produto;
import com.staya.api.repository.ConsumoRepository;
import com.staya.api.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ConsumoRepository consumoRepository;

    public ProdutoService(ProdutoRepository produtoRepository, ConsumoRepository consumoRepository) {
        this.produtoRepository = produtoRepository;
        this.consumoRepository = consumoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listar(CategoriaProduto categoria, Boolean ativo) {
        return produtoRepository.buscar(categoria, ativo).stream().map(ProdutoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscar(Long id) {
        return ProdutoMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        validarNomeUnico(dto.getNome().trim(), null);
        Produto produto = new Produto();
        ProdutoMapper.copiar(dto, produto);
        return ProdutoMapper.toResponse(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produto = buscarEntidade(id);
        validarNomeUnico(dto.getNome().trim(), id);
        ProdutoMapper.copiar(dto, produto);
        return ProdutoMapper.toResponse(produto);
    }

    @Transactional
    public void excluir(Long id) {
        Produto produto = buscarEntidade(id);
        if (consumoRepository.existsByProdutoId(id)) {
            throw new ConflitoException("Produto já foi lançado em contas e não pode ser excluído; desative-o (ativo = false)");
        }
        produtoRepository.delete(produto);
    }

    Produto buscarEntidade(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));
    }

    private void validarNomeUnico(String nome, Long idAtual) {
        boolean existe = idAtual == null
                ? produtoRepository.existsByNomeIgnoreCase(nome)
                : produtoRepository.existsByNomeIgnoreCaseAndIdNot(nome, idAtual);
        if (existe) {
            throw new ConflitoException("Já existe um produto com o nome '" + nome + "'");
        }
    }
}
