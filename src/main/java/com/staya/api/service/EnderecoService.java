package com.staya.api.service;

import com.staya.api.dto.EnderecoRequestDTO;
import com.staya.api.dto.EnderecoResponseDTO;
import com.staya.api.exception.ConflitoException;
import com.staya.api.exception.RecursoNaoEncontradoException;
import com.staya.api.mapper.EnderecoMapper;
import com.staya.api.model.Endereco;
import com.staya.api.repository.ClienteRepository;
import com.staya.api.repository.EnderecoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final ClienteRepository clienteRepository;

    public EnderecoService(EnderecoRepository enderecoRepository, ClienteRepository clienteRepository) {
        this.enderecoRepository = enderecoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<EnderecoResponseDTO> listar() {
        return enderecoRepository.findAll().stream().map(EnderecoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EnderecoResponseDTO buscar(Long id) {
        return EnderecoMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public EnderecoResponseDTO criar(EnderecoRequestDTO dto) {
        Endereco endereco = new Endereco();
        EnderecoMapper.copiar(dto, endereco);
        return EnderecoMapper.toResponse(enderecoRepository.save(endereco));
    }

    @Transactional
    public EnderecoResponseDTO atualizar(Long id, EnderecoRequestDTO dto) {
        Endereco endereco = buscarEntidade(id);
        EnderecoMapper.copiar(dto, endereco);
        return EnderecoMapper.toResponse(endereco);
    }

    @Transactional
    public void excluir(Long id) {
        Endereco endereco = buscarEntidade(id);
        if (clienteRepository.existsByEnderecoId(id)) {
            throw new ConflitoException("Endereço está vinculado a um ou mais clientes e não pode ser excluído");
        }
        enderecoRepository.delete(endereco);
    }

    Endereco buscarEntidade(Long id) {
        return enderecoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço", id));
    }
}
