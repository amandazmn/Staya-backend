package com.staya.api.model;

import java.util.EnumSet;
import java.util.Set;

/** Ciclo de vida de uma reserva: PENDENTE → CONFIRMADA → EM_HOSPEDAGEM → CONCLUIDA (ou CANCELADA). */
public enum StatusReserva {
    /** Criada, aguardando confirmação (ex.: pagamento do sinal). */
    PENDENTE,
    /** Confirmada pelo hotel; pronta para o check-in. */
    CONFIRMADA,
    /** Check-in realizado; hóspede no hotel. */
    EM_HOSPEDAGEM,
    /** Cancelada; libera o quarto no período. */
    CANCELADA,
    /** Check-out realizado. */
    CONCLUIDA;

    /** Status que ocupam o quarto no período e, portanto, bloqueiam novas reservas. */
    public static final Set<StatusReserva> ATIVOS = EnumSet.of(PENDENTE, CONFIRMADA, EM_HOSPEDAGEM);

    /** Status em que a reserva ainda pode ser alterada ou cancelada (antes do check-in). */
    public static final Set<StatusReserva> EDITAVEIS = EnumSet.of(PENDENTE, CONFIRMADA);

    public boolean isEditavel() {
        return EDITAVEIS.contains(this);
    }
}
