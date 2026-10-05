package com.ctrindadedev.iia.core.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um turno operacional (psoe) e agrega
 * os registros lançados durante o turno (tanques, movimentações, paradas) e o
 * resumo gerado pela IA ao fechamento diario
 *
 */
@Entity
@Table(name = "passagem_servico_operador_estacao")
@Getter
@Setter
@NoArgsConstructor
public class PassagemServicoOperadorEstacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime inicioTurno;

    private LocalDateTime fimTurno;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTurno status = StatusTurno.EM_ANDAMENTO;

    @Column(nullable = false)
    private String responsavel;

    @OneToMany(mappedBy = "passagemServicoOperadorEstacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroTanque> registrosTanque = new ArrayList<>();

    @OneToMany(mappedBy = "passagemServicoOperadorEstacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimentacaoFluido> movimentacoesFluido = new ArrayList<>();

    @OneToMany(mappedBy = "passagemServicoOperadorEstacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParadaEquipamento> paradasEquipamento = new ArrayList<>();

    @OneToOne(mappedBy = "passagemServicoOperadorEstacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private ResumoOperacional resumoOperacional;

    public enum StatusTurno {
        EM_ANDAMENTO,
        FECHADO
    }
}
