CREATE TABLE reservas (
    id_reserva           BIGINT         NOT NULL AUTO_INCREMENT,
    fk_cliente           BIGINT         NOT NULL,
    fk_quarto            BIGINT         NOT NULL,
    data_entrada         DATE           NOT NULL,
    data_saida           DATE           NOT NULL,
    quantidade_hospedes  INT            NOT NULL,
    status               VARCHAR(20)    NOT NULL,
    valor_diaria         DECIMAL(10, 2) NOT NULL,
    valor_total          DECIMAL(12, 2) NOT NULL,
    observacoes          VARCHAR(255),
    criado_em            DATETIME(6)    NOT NULL,
    CONSTRAINT pk_reservas PRIMARY KEY (id_reserva),
    CONSTRAINT fk_reservas_cliente FOREIGN KEY (fk_cliente) REFERENCES clientes (id_cliente),
    CONSTRAINT fk_reservas_quarto FOREIGN KEY (fk_quarto) REFERENCES quartos (id_quarto),
    CONSTRAINT ck_reservas_periodo CHECK (data_saida > data_entrada),
    CONSTRAINT ck_reservas_hospedes CHECK (quantidade_hospedes > 0)
);

-- Acelera a verificação de conflito de datas por quarto
CREATE INDEX idx_reservas_quarto_periodo ON reservas (fk_quarto, data_entrada, data_saida);
