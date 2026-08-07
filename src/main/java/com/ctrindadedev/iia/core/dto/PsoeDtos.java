package com.ctrindadedev.iia.core.dto;

import com.ctrindadedev.iia.core.domain.PassagemServicoOperadorEstacao;

import java.time.LocalDateTime;

/**
 * DTOs de entrada/saída do fluxo de PSOE (abertura, registros e fechamento
 * de turno) e do chat. Agrupados aqui por serem simples e usados só na
 * borda (controller).
 */
public class PsoeDtos {

    /**
     * {@code dataTurno} é opcional — só existe pra permitir simular, em
     * demonstração, um turno com data no passado (ex.: "ontem"), sem
     * precisar esperar o relógio real andar. Se omitido, usa o instante atual.
     */
    public record AbrirTurnoRequest(String responsavel, LocalDateTime dataTurno) {
    }

    public record RegistroTanqueRequest(String identificacaoTanque, Double nivelPercentual) {
    }

    public record MovimentacaoFluidoRequest(String tipoFluido, Double volume) {
    }

    public record ParadaEquipamentoRequest(String equipamento, String motivo) {
    }

    public record PerguntaRequest(String conversationId, String mensagem) {
    }

    public record RespostaChat(String resposta) {
    }

    public record TurnoResponse(
            Long id,
            String responsavel,
            String status,
            LocalDateTime inicioTurno,
            LocalDateTime fimTurno,
            String resumoOperacional
    ) {
        public static TurnoResponse from(PassagemServicoOperadorEstacao turno) {
            var resumo = turno.getResumoOperacional();
            return new TurnoResponse(
                    turno.getId(),
                    turno.getResponsavel(),
                    turno.getStatus().name(),
                    turno.getInicioTurno(),
                    turno.getFimTurno(),
                    resumo != null ? resumo.getTextoResumo() : null
            );
        }
    }
}
