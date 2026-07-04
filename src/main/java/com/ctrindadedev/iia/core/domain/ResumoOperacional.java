package com.ctrindadedev.iia.core.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Armazena o texto do resumo operacional gerado pela IA ao fechar o
 * turno. É o ponto de contato entre os dois domínios (ia e o crud)
 * {@code textoResumo} a partir do {@link PassagemServicoOperadorEstacao};
 */
@Entity
@Table(name = "resumo_operacional")
@Getter
@Setter
@NoArgsConstructor
public class ResumoOperacional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passagem_servico_id")
    private PassagemServicoOperadorEstacao passagemServicoOperadorEstacao;

    @Lob
    @Column(nullable = false)
    private String textoResumo;

    private LocalDateTime geradoEm;
}
