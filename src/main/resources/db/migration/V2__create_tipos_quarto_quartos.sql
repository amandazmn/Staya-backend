CREATE TABLE tipos_quarto (
    id_tipo_quarto  BIGINT         NOT NULL AUTO_INCREMENT,
    nome            VARCHAR(50)    NOT NULL,
    descricao       VARCHAR(255),
    capacidade      INT            NOT NULL,
    tarifa_base     DECIMAL(10, 2) NOT NULL,
    CONSTRAINT pk_tipos_quarto PRIMARY KEY (id_tipo_quarto),
    CONSTRAINT uk_tipos_quarto_nome UNIQUE (nome),
    CONSTRAINT ck_tipos_quarto_capacidade CHECK (capacidade > 0),
    CONSTRAINT ck_tipos_quarto_tarifa CHECK (tarifa_base >= 0)
);

CREATE TABLE quartos (
    id_quarto       BIGINT       NOT NULL AUTO_INCREMENT,
    numero          VARCHAR(10)  NOT NULL,
    andar           INT,
    status          VARCHAR(20)  NOT NULL DEFAULT 'DISPONIVEL',
    observacoes     VARCHAR(255),
    fk_tipo_quarto  BIGINT       NOT NULL,
    CONSTRAINT pk_quartos PRIMARY KEY (id_quarto),
    CONSTRAINT uk_quartos_numero UNIQUE (numero),
    CONSTRAINT fk_quartos_tipo_quarto FOREIGN KEY (fk_tipo_quarto) REFERENCES tipos_quarto (id_tipo_quarto)
);
