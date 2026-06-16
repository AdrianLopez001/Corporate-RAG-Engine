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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    private static final int TOP_K = 4;
    private static final double SIMILARITY_THRESHOLD = 0.70;

    private static final String SYSTEM_PROMPT = """
            You are an expert corporate virtual assistant.
            Answer the user's question using ONLY the context provided below.
            If the answer cannot be found in the context, politely state that you do not have that information.
            Do not fabricate information. Be objective and precise.

            CONTEXT:
            {context}
            """;

    public ChatResponse askQuestion(String userQuery) {
        log.debug("Processing query: {}", userQuery);

        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(userQuery)
                        .topK(TOP_K)
                        .similarityThreshold(SIMILARITY_THRESHOLD)
                        .build()
        );

        log.debug("Found {} relevant document(s) for query.", relevantDocs.size());

        String context = relevantDocs.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n\n"));

        List<String> sources = relevantDocs.stream()
                .map(doc -> (String) doc.getMetadata().getOrDefault("source", "unknown"))
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
