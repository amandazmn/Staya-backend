package com.staya.api.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClienteRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 50)
    private String nome;

    @NotBlank(message = "Sobrenome é obrigatório")
    @Size(max = 100)
    private String sobrenome;

    @Pattern(regexp = "^$|\\d{11}", message = "CPF deve conter exatamente 11 dígitos numéricos")
    private String cpf;

    @Size(max = 20)
    private String passaporte;

    @Email(message = "Email inválido")
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String telefone;

    @NotBlank(message = "Nacionalidade é obrigatória")
    @Size(max = 50)
    private String nacionalidade;

    private Long enderecoId;

    public ClienteRequestDTO() {}

    /**
     * Todo hóspede precisa de ao menos um documento: CPF (brasileiros) ou passaporte (estrangeiros).
     */
    @AssertTrue(message = "Informe o CPF ou o passaporte")
    public boolean isDocumentoInformado() {
        return (cpf != null && !cpf.isBlank()) || (passaporte != null && !passaporte.isBlank());
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPassaporte() {
        return passaporte;
    }

    public void setPassaporte(String passaporte) {
        this.passaporte = passaporte;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public Long getEnderecoId() {
        return enderecoId;
    }

    public void setEnderecoId(Long enderecoId) {
        this.enderecoId = enderecoId;
    }
}
