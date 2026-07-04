package com.ctrindadedev.iia.core.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Parada de equipamento (planejada ou não) ocorrida durante a psoe
 */
@Entity
@Table(name = "parada_equipamento")
@Getter
@Setter
@NoArgsConstructor
public class ParadaEquipamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passagem_servico_id")
    private PassagemServicoOperadorEstacao passagemServicoOperadorEstacao;

    @Column(nullable = false)
    private String equipamento;

    private String motivo;
}
