package com.ctrindadedev.iia.core.service;

import com.ctrindadedev.iia.ai.service.FechamentoTurnoService;
import com.ctrindadedev.iia.core.domain.MovimentacaoFluido;
import com.ctrindadedev.iia.core.domain.ParadaEquipamento;
import com.ctrindadedev.iia.core.domain.PassagemServicoOperadorEstacao;
import com.ctrindadedev.iia.core.domain.PassagemServicoOperadorEstacao.StatusTurno;
import com.ctrindadedev.iia.core.domain.RegistroTanque;
import com.ctrindadedev.iia.core.dto.PsoeDtos.AbrirTurnoRequest;
import com.ctrindadedev.iia.core.dto.PsoeDtos.MovimentacaoFluidoRequest;
import com.ctrindadedev.iia.core.dto.PsoeDtos.ParadaEquipamentoRequest;
import com.ctrindadedev.iia.core.dto.PsoeDtos.RegistroTanqueRequest;
import com.ctrindadedev.iia.core.repository.PassagemServicoOperadorEstacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Orquestra o ciclo de vida da PSOE: abertura, lançamento de registros
 * durante o turno, e fechamento — que é o gatilho para a geração do resumo
 * operacional via IA e sua indexação no vector store (ver
 * {@link FechamentoTurnoService}).
 */
@Service
public class PsoeService {

    private final PassagemServicoOperadorEstacaoRepository repository;
    private final FechamentoTurnoService fechamentoTurnoService;

    public PsoeService(PassagemServicoOperadorEstacaoRepository repository,
                        FechamentoTurnoService fechamentoTurnoService) {
        this.repository = repository;
        this.fechamentoTurnoService = fechamentoTurnoService;
    }

    @Transactional
    public PassagemServicoOperadorEstacao abrirTurno(AbrirTurnoRequest request) {
        var turno = new PassagemServicoOperadorEstacao();
        turno.setResponsavel(request.responsavel());
        turno.setInicioTurno(request.dataTurno() != null ? request.dataTurno() : LocalDateTime.now());
        turno.setStatus(StatusTurno.EM_ANDAMENTO);
        return repository.save(turno);
    }

    @Transactional
    public PassagemServicoOperadorEstacao adicionarRegistroTanque(Long turnoId, RegistroTanqueRequest request) {
        var turno = buscarTurnoAberto(turnoId);
        var registro = new RegistroTanque();
        registro.setPassagemServicoOperadorEstacao(turno);
        registro.setIdentificacaoTanque(request.identificacaoTanque());
        registro.setNivelPercentual(request.nivelPercentual());
        turno.getRegistrosTanque().add(registro);
        return repository.save(turno);
    }

    @Transactional
    public PassagemServicoOperadorEstacao adicionarMovimentacaoFluido(Long turnoId, MovimentacaoFluidoRequest request) {
        var turno = buscarTurnoAberto(turnoId);
        var movimentacao = new MovimentacaoFluido();
        movimentacao.setPassagemServicoOperadorEstacao(turno);
        movimentacao.setTipoFluido(request.tipoFluido());
        movimentacao.setVolume(request.volume());
        turno.getMovimentacoesFluido().add(movimentacao);
        return repository.save(turno);
    }

    @Transactional
    public PassagemServicoOperadorEstacao adicionarParadaEquipamento(Long turnoId, ParadaEquipamentoRequest request) {
        var turno = buscarTurnoAberto(turnoId);
        var parada = new ParadaEquipamento();
        parada.setPassagemServicoOperadorEstacao(turno);
        parada.setEquipamento(request.equipamento());
        parada.setMotivo(request.motivo());
        turno.getParadasEquipamento().add(parada);
        return repository.save(turno);
    }

    @Transactional
    public PassagemServicoOperadorEstacao fecharTurno(Long turnoId) {
        var turno = buscarTurnoAberto(turnoId);
        turno.setFimTurno(LocalDateTime.now());
        turno.setStatus(StatusTurno.FECHADO);
        var turnoFechado = repository.save(turno);

        // Gera o resumo via IA e indexa no vector store para consulta futura via RAG
        fechamentoTurnoService.gerarResumoEIndexar(turnoFechado);

        return turnoFechado;
    }

    public PassagemServicoOperadorEstacao buscar(Long turnoId) {
        return repository.findById(turnoId)
                .orElseThrow(() -> new IllegalArgumentException("Turno não encontrado: " + turnoId));
    }

    private PassagemServicoOperadorEstacao buscarTurnoAberto(Long turnoId) {
        var turno = buscar(turnoId);
        if (turno.getStatus() != StatusTurno.EM_ANDAMENTO) {
            throw new IllegalStateException("Turno " + turnoId + " já está fechado e não aceita novos registros.");
        }
        return turno;
    }
}
