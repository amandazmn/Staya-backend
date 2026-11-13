package com.staya.api.service;

import com.staya.api.dto.ClienteRequestDTO;
import com.staya.api.dto.ClienteResponseDTO;
import com.staya.api.exception.ConflitoException;
import com.staya.api.exception.RecursoNaoEncontradoException;
import com.staya.api.mapper.ClienteMapper;
import com.staya.api.mapper.Textos;
import com.staya.api.model.Cliente;
import com.staya.api.repository.ClienteRepository;
import com.staya.api.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoService enderecoService;
    private final ReservaRepository reservaRepository;

    public ClienteService(ClienteRepository clienteRepository, EnderecoService enderecoService,
                          ReservaRepository reservaRepository) {
        this.clienteRepository = clienteRepository;
        this.enderecoService = enderecoService;
        this.reservaRepository = reservaRepository;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listar() {
        return clienteRepository.findAll().stream().map(ClienteMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscar(Long id) {
        return ClienteMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ClienteResponseDTO criar(ClienteRequestDTO dto) {
        validarDocumentosUnicos(dto, null);
        Cliente cliente = new Cliente();
        preencher(dto, cliente);
        return ClienteMapper.toResponse(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = buscarEntidade(id);
        validarDocumentosUnicos(dto, id);
        preencher(dto, cliente);
        return ClienteMapper.toResponse(cliente);
    }

    @Transactional
    public void excluir(Long id) {
        Cliente cliente = buscarEntidade(id);
        if (reservaRepository.existsByClienteId(id)) {
            throw new ConflitoException("Cliente possui reservas registradas e não pode ser excluído");
        }
        clienteRepository.delete(cliente);
    }

    private void preencher(ClienteRequestDTO dto, Cliente cliente) {
        ClienteMapper.copiar(dto, cliente);
        cliente.setEndereco(dto.getEnderecoId() == null ? null : enderecoService.buscarEntidade(dto.getEnderecoId()));
    }

    private void validarDocumentosUnicos(ClienteRequestDTO dto, Long idAtual) {
        String cpf = Textos.limpar(dto.getCpf());
        if (cpf != null && (idAtual == null
                ? clienteRepository.existsByCpf(cpf)
                : clienteRepository.existsByCpfAndIdNot(cpf, idAtual))) {
            throw new ConflitoException("Já existe um cliente cadastrado com este CPF");
        }
        String passaporte = Textos.limpar(dto.getPassaporte());
        if (passaporte != null && (idAtual == null
                ? clienteRepository.existsByPassaporte(passaporte)
                : clienteRepository.existsByPassaporteAndIdNot(passaporte, idAtual))) {
            throw new ConflitoException("Já existe um cliente cadastrado com este passaporte");
        }
    }

    Cliente buscarEntidade(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", id));
    }
}
