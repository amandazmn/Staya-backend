package com.staya.api.model;

import java.util.EnumSet;
import java.util.Set;

/** Ciclo de vida de uma reserva. */
public enum StatusReserva {
    /** Criada, aguardando confirmação (ex.: pagamento do sinal). */
    PENDENTE,
    /** Confirmada pelo hotel. */
    CONFIRMADA,
    /** Cancelada; libera o quarto no período. */
    CANCELADA,
    /** Estadia encerrada (definido pelo check-out). */
    CONCLUIDA;

    /** Status que ocupam o quarto no período e, portanto, bloqueiam novas reservas. */
    public static final Set<StatusReserva> ATIVOS = EnumSet.of(PENDENTE, CONFIRMADA);

    public boolean isAtiva() {
        return ATIVOS.contains(this);
    }
}
