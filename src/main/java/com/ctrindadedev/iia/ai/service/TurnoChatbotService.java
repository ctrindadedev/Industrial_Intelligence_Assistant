package com.ctrindadedev.iia.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Responsável pelo endpoint de chat com RAG
 * sobre os resumos operacionais de turnos anteriores, com memória de
 * conversação por sessão/usuário.
 */
@Service
public class TurnoChatbotService {

    private static final String SYSTEM_TEMPLATE = """
            Você é um assistente de operações industriais. Hoje é {hoje}.

            Responda perguntas sobre passagens de serviço anteriores utilizando
            EXCLUSIVAMENTE o contexto recuperado dos registros. Nunca invente
            equipamentos, valores, eventos ou datas que não estejam no contexto.

            Se a pergunta se referir a uma data posterior a hoje ({hoje}), deixe
            claro que esse turno ainda não aconteceu e por isso não existe
            registro para ele — não tente responder com base em turnos
            anteriores como se fossem daquele dia.

            Se o contexto recuperado não tiver informação suficiente para
            responder, diga explicitamente que não possui esse dado nos
            registros disponíveis, em vez de arriscar uma resposta.
            """;

    private final ChatClient chatClient;

    public TurnoChatbotService(ChatClient.Builder chatClientBuilder,
                                VectorStore vectorStore,
                                ChatMemory chatMemory) {

        var documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(0.5)
                .topK(5)
                .build();

        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        RetrievalAugmentationAdvisor.builder()
                                .documentRetriever(documentRetriever)
                                .build(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     *  quando expor no controller,  solicitar um identificador de
     *  conversa para o ChatMemory manter o histórico por sessão de usuario
     *  O system prompt é montado por requisição (não fixado no builder) para
     *  que "hoje" reflita sempre a data real no momento da pergunta.
     */
    public String perguntar(String conversationId, String mensagem) {
        return chatClient.prompt()
                .system(spec -> spec.text(SYSTEM_TEMPLATE).param("hoje", LocalDate.now().toString()))
                .user(mensagem)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}
