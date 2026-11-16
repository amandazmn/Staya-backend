-- Catálogo de itens que podem ser lançados na conta (frigobar, restaurante, lavanderia...)
CREATE TABLE produtos (
    id_produto  BIGINT         NOT NULL AUTO_INCREMENT,
    nome        VARCHAR(100)   NOT NULL,
    categoria   VARCHAR(20)    NOT NULL,
    preco       DECIMAL(10, 2) NOT NULL,
    ativo       BOOLEAN        NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_produtos PRIMARY KEY (id_produto),
    CONSTRAINT uk_produtos_nome UNIQUE (nome),
    CONSTRAINT ck_produtos_preco CHECK (preco >= 0)
);

-- Lançamentos não são apagados: um lançamento errado é estornado (estornado_em preenchido)
CREATE TABLE consumos (
    id_consumo      BIGINT         NOT NULL AUTO_INCREMENT,
    fk_estadia      BIGINT         NOT NULL,
    fk_produto      BIGINT         NOT NULL,
    descricao       VARCHAR(100)   NOT NULL,
    quantidade      INT            NOT NULL,
    valor_unitario  DECIMAL(10, 2) NOT NULL,
    valor_total     DECIMAL(12, 2) NOT NULL,
    lancado_em      DATETIME(6)    NOT NULL,
    observacoes     VARCHAR(255),
    estornado_em    DATETIME(6),
    motivo_estorno  VARCHAR(255),
    CONSTRAINT pk_consumos PRIMARY KEY (id_consumo),
    CONSTRAINT fk_consumos_estadia FOREIGN KEY (fk_estadia) REFERENCES estadias (id_estadia),
    CONSTRAINT fk_consumos_produto FOREIGN KEY (fk_produto) REFERENCES produtos (id_produto),
    CONSTRAINT ck_consumos_quantidade CHECK (quantidade > 0)
);

CREATE TABLE pagamentos (
    id_pagamento    BIGINT         NOT NULL AUTO_INCREMENT,
    fk_estadia      BIGINT         NOT NULL,
    forma           VARCHAR(20)    NOT NULL,
    valor           DECIMAL(12, 2) NOT NULL,
    pago_em         DATETIME(6)    NOT NULL,
    observacoes     VARCHAR(255),
    estornado_em    DATETIME(6),
    motivo_estorno  VARCHAR(255),
    CONSTRAINT pk_pagamentos PRIMARY KEY (id_pagamento),
    CONSTRAINT fk_pagamentos_estadia FOREIGN KEY (fk_estadia) REFERENCES estadias (id_estadia),
    CONSTRAINT ck_pagamentos_valor CHECK (valor > 0)
);
