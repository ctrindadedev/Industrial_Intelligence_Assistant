package com.ctrindadedev.iia.core.controller;

import com.ctrindadedev.iia.core.dto.PsoeDtos.AbrirTurnoRequest;
import com.ctrindadedev.iia.core.dto.PsoeDtos.MovimentacaoFluidoRequest;
import com.ctrindadedev.iia.core.dto.PsoeDtos.ParadaEquipamentoRequest;
import com.ctrindadedev.iia.core.dto.PsoeDtos.RegistroTanqueRequest;
import com.ctrindadedev.iia.core.dto.PsoeDtos.TurnoResponse;
import com.ctrindadedev.iia.core.service.PsoeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/psoe")
public class PsoeController {

    private final PsoeService psoeService;

    public PsoeController(PsoeService psoeService) {
        this.psoeService = psoeService;
    }

    @PostMapping
    public ResponseEntity<TurnoResponse> abrirTurno(@RequestBody AbrirTurnoRequest request) {
        var turno = psoeService.abrirTurno(request);
        return ResponseEntity.ok(TurnoResponse.from(turno));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurnoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(TurnoResponse.from(psoeService.buscar(id)));
    }

    @PostMapping("/{id}/tanques")
    public ResponseEntity<TurnoResponse> adicionarRegistroTanque(@PathVariable Long id,
                                                                   @RequestBody RegistroTanqueRequest request) {
        var turno = psoeService.adicionarRegistroTanque(id, request);
        return ResponseEntity.ok(TurnoResponse.from(turno));
    }

    @PostMapping("/{id}/movimentacoes")
    public ResponseEntity<TurnoResponse> adicionarMovimentacao(@PathVariable Long id,
                                                                  @RequestBody MovimentacaoFluidoRequest request) {
        var turno = psoeService.adicionarMovimentacaoFluido(id, request);
        return ResponseEntity.ok(TurnoResponse.from(turno));
    }

    @PostMapping("/{id}/paradas")
    public ResponseEntity<TurnoResponse> adicionarParada(@PathVariable Long id,
                                                            @RequestBody ParadaEquipamentoRequest request) {
        var turno = psoeService.adicionarParadaEquipamento(id, request);
        return ResponseEntity.ok(TurnoResponse.from(turno));
    }

    @PostMapping("/{id}/fechar")
    public ResponseEntity<TurnoResponse> fecharTurno(@PathVariable Long id) {
        var turno = psoeService.fecharTurno(id);
        return ResponseEntity.ok(TurnoResponse.from(turno));
    }
}
