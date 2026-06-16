package com.enterprise.rag.service;

import com.enterprise.rag.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    private static final int TOP_K = 4;
    private static final double SIMILARITY_THRESHOLD = 0.70;

    private static final String SYSTEM_PROMPT = """
            Você é um assistente virtual corporativo especialista.
            Responda a pergunta do usuário utilizando APENAS o contexto fornecido abaixo.
            Se a resposta não puder ser encontrada no contexto, diga educadamente que não possui essa informação.
            Não invente informações. Seja objetivo e preciso.

            CONTEXTO:
            {context}
            """;

    public ChatResponse askQuestion(String userQuery) {
        log.debug("Processando query: {}", userQuery);

        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(userQuery)
                        .topK(TOP_K)
                        .similarityThreshold(SIMILARITY_THRESHOLD)
                        .build()
        );

        log.debug("{} documento(s) relevante(s) encontrado(s) para a query.", relevantDocs.size());

        String context = relevantDocs.stream()
                .map(Document::getFormattedContent)
                .reduce("", (a, b) -> a + "\n\n" + b);

        List<String> sources = relevantDocs.stream()
                .map(doc -> (String) doc.getMetadata().getOrDefault("source", "desconhecido"))
                .distinct()
                .toList();

        String answer = chatClient.prompt()
                .system(sp -> sp.param("context", context))
                .user(userQuery)
                .call()
                .content();

        return new ChatResponse(answer, sources);
    }
}
