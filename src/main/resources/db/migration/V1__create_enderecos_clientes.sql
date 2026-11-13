CREATE TABLE enderecos (
    id_endereco  BIGINT       NOT NULL AUTO_INCREMENT,
    logradouro   VARCHAR(100) NOT NULL,
    numero       VARCHAR(10),
    complemento  VARCHAR(50),
    bairro       VARCHAR(50),
    cidade       VARCHAR(50)  NOT NULL,
    estado       VARCHAR(50),
    pais         VARCHAR(50)  NOT NULL,
    cep          VARCHAR(15),
    CONSTRAINT pk_enderecos PRIMARY KEY (id_endereco)
);

CREATE TABLE clientes (
    id_cliente     BIGINT       NOT NULL AUTO_INCREMENT,
    nome           VARCHAR(50)  NOT NULL,
    sobrenome      VARCHAR(100) NOT NULL,
    cpf            VARCHAR(11),
    passaporte     VARCHAR(20),
    email          VARCHAR(100),
    telefone       VARCHAR(20),
    nacionalidade  VARCHAR(50)  NOT NULL,
    fk_endereco    BIGINT,
    CONSTRAINT pk_clientes PRIMARY KEY (id_cliente),
    CONSTRAINT uk_clientes_cpf UNIQUE (cpf),
    CONSTRAINT uk_clientes_passaporte UNIQUE (passaporte),
    CONSTRAINT fk_clientes_endereco FOREIGN KEY (fk_endereco) REFERENCES enderecos (id_endereco)
);
