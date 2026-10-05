package com.ctrindadedev.iia.ai.service;

import com.ctrindadedev.iia.core.domain.PassagemServicoOperadorEstacao;
import com.ctrindadedev.iia.core.domain.ResumoOperacional;
import com.ctrindadedev.iia.core.repository.PassagemServicoOperadorEstacaoRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Responsável pelo "fechamento inteligente" do turno: a partir dos dados
 * brutos lançados durante a PSOE (tanques, movimentações, paradas), gera um
 * resumo operacional em linguagem natural via IA, persiste na entidade
 * {@link ResumoOperacional} e indexa o texto no vector store — é esse
 * documento que o {@link TurnoChatbotService} recupera depois via RAG.
 * O prompt é deliberadamente restrito a "resuma só o que está nos dados
 * abaixo" para não puxar conhecimento externo do modelo para dentro do
 * resumo (mesmo princípio de "não inventar" aplicado já na geração).
 */
@Service
public class FechamentoTurnoService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final PassagemServicoOperadorEstacaoRepository repository;

    public FechamentoTurnoService(ChatClient.Builder chatClientBuilder,
                                   VectorStore vectorStore,
                                   PassagemServicoOperadorEstacaoRepository repository) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        Você resume passagens de serviço (turnos) de uma estação de petróleo
                        e gás. Gere um resumo objetivo, em português, em um único parágrafo
                        corrido, com base EXCLUSIVAMENTE nos dados fornecidos pelo usuário.
                        Não invente equipamentos, valores ou eventos que não estejam nos
                        dados. Sempre cite a data do turno e o responsável no início do
                        resumo.
                        """)
                .build();
        this.vectorStore = vectorStore;
        this.repository = repository;
    }

    public void gerarResumoEIndexar(PassagemServicoOperadorEstacao turno) {
        String dadosBrutos = montarDadosBrutos(turno);

        String textoResumo = chatClient.prompt()
                .user(dadosBrutos)
                .call()
                .content();

        var resumo = new ResumoOperacional();
        resumo.setPassagemServicoOperadorEstacao(turno);
        resumo.setTextoResumo(textoResumo);
        resumo.setGeradoEm(LocalDateTime.now());
        turno.setResumoOperacional(resumo);
        repository.save(turno);

        indexarNoVectorStore(turno, textoResumo);
    }

    private String montarDadosBrutos(PassagemServicoOperadorEstacao turno) {
        StringBuilder sb = new StringBuilder();
        sb.append("Data do turno: ").append(turno.getInicioTurno().toLocalDate()).append('\n');
        sb.append("Responsável: ").append(turno.getResponsavel()).append('\n');

        sb.append("Registros de tanque:\n");
        if (turno.getRegistrosTanque().isEmpty()) {
            sb.append("- Nenhum registro de tanque no turno.\n");
        } else {
            turno.getRegistrosTanque().forEach(r ->
                    sb.append("- Tanque ").append(r.getIdentificacaoTanque())
                            .append(": ").append(r.getNivelPercentual()).append("%\n"));
        }

        sb.append("Movimentações de fluido:\n");
        if (turno.getMovimentacoesFluido().isEmpty()) {
            sb.append("- Nenhuma movimentação registrada no turno.\n");
        } else {
            turno.getMovimentacoesFluido().forEach(m ->
                    sb.append("- ").append(m.getTipoFluido())
                            .append(": ").append(m.getVolume()).append(" m³\n"));
        }

        sb.append("Paradas de equipamento:\n");
        if (turno.getParadasEquipamento().isEmpty()) {
            sb.append("- Nenhuma parada de equipamento registrada no turno.\n");
        } else {
            turno.getParadasEquipamento().forEach(p ->
                    sb.append("- ").append(p.getEquipamento())
                            .append(" (motivo: ").append(p.getMotivo()).append(")\n"));
        }

        return sb.toString();
    }

    private void indexarNoVectorStore(PassagemServicoOperadorEstacao turno, String textoResumo) {
        Map<String, Object> metadata = Map.of(
                "turnoId", turno.getId(),
                "data", turno.getInicioTurno().toLocalDate().toString(),
                "responsavel", turno.getResponsavel()
        );
        var document = new Document(textoResumo, metadata);
        vectorStore.add(List.of(document));
    }
}
