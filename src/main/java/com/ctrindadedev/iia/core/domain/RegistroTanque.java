package com.ctrindadedev.iia.core.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Registro de nível/condição de um tanque durante a psoe
 */
@Entity
@Table(name = "registro_tanque")
@Getter
@Setter
@NoArgsConstructor
public class RegistroTanque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passagem_servico_id")
    private PassagemServicoOperadorEstacao passagemServicoOperadorEstacao;

    @Column(nullable = false)
    private String identificacaoTanque;

    private Double nivelPercentual;
}
