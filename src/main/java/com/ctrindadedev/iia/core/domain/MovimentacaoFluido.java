package com.ctrindadedev.iia.core.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Movimentação de fluido (entrada/saída) registrada numa psoe
 * criar enum para isso
 */
@Entity
@Table(name = "movimentacao_fluido")
@Getter
@Setter
@NoArgsConstructor
public class MovimentacaoFluido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passagem_servico_id")
    private PassagemServicoOperadorEstacao passagemServicoOperadorEstacao;

    @Column(nullable = false)
    private String tipoFluido;

    private Double volume;
}
