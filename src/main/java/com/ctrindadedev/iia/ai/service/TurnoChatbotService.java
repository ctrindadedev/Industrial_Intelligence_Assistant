package com.ctrindadedev.iia.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

/**
 * Responsável pelo endpoint de chat com RAG
 * sobre os resumos operacionais de turnos anteriores, com memória de
 * conversação por sessão/usuário.
 */
@Service
public class TurnoChatbotService {

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
                .defaultSystem("""
                        Você é um assistente de operações industriais. Responda perguntas
                        sobre passagens de serviço anteriores utilizando exclusivamente o
                        contexto recuperado. Se não souber a resposta, diga que não possui
                        essa informação nos registros disponíveis.
                        """)
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
     */
    public String perguntar(String conversationId, String mensagem) {
        return chatClient.prompt()
                .user(mensagem)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}
