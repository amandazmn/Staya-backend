CREATE TABLE estadias (
    id_estadia   BIGINT       NOT NULL AUTO_INCREMENT,
    fk_reserva   BIGINT       NOT NULL,
    checkin_em   DATETIME(6)  NOT NULL,
    checkout_em  DATETIME(6),
    status       VARCHAR(20)  NOT NULL,
    observacoes  VARCHAR(255),
    CONSTRAINT pk_estadias PRIMARY KEY (id_estadia),
    -- Uma reserva gera no máximo uma estadia (protege contra check-in duplicado)
    CONSTRAINT uk_estadias_reserva UNIQUE (fk_reserva),
    CONSTRAINT fk_estadias_reserva FOREIGN KEY (fk_reserva) REFERENCES reservas (id_reserva)
);

-- Demais hóspedes do quarto além do titular da reserva (registro obrigatório na FNRH)
CREATE TABLE estadia_acompanhantes (
    fk_estadia  BIGINT NOT NULL,
    fk_cliente  BIGINT NOT NULL,
    CONSTRAINT pk_estadia_acompanhantes PRIMARY KEY (fk_estadia, fk_cliente),
    CONSTRAINT fk_estadia_acompanhantes_estadia FOREIGN KEY (fk_estadia) REFERENCES estadias (id_estadia),
    CONSTRAINT fk_estadia_acompanhantes_cliente FOREIGN KEY (fk_cliente) REFERENCES clientes (id_cliente)
);
