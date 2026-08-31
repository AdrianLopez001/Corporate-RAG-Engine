package com.enterprise.rag.service;

import com.enterprise.rag.dto.ChatResponse;
import com.enterprise.rag.dto.ChunkDetail;
import com.enterprise.rag.dto.QueryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
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

    public ChatResponse askQuestion(QueryRequest request) {
        String userQuery = request.query();
        String documentFilter = request.documentFilter();
        log.debug("Processing query: {} (Filter: {})", userQuery, documentFilter);

        SearchRequest.Builder builder = SearchRequest.builder()
                .query(userQuery)
                .topK(TOP_K)
                .similarityThreshold(SIMILARITY_THRESHOLD);

        if (documentFilter != null && !documentFilter.isBlank() && !"ALL".equalsIgnoreCase(documentFilter)) {
            builder.filterExpression("source == '" + documentFilter.replace("'", "") + "'");
        }

        List<Document> relevantDocs = vectorStore.similaritySearch(builder.build());
        log.debug("Found {} relevant document(s) for query.", relevantDocs.size());

        String context = relevantDocs.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n\n"));

        List<String> sources = relevantDocs.stream()
                .map(doc -> (String) doc.getMetadata().getOrDefault("source", "unknown"))
                .distinct()
                .toList();

        List<ChunkDetail> chunks = new ArrayList<>();
        for (Document doc : relevantDocs) {
            String source = (String) doc.getMetadata().getOrDefault("source", "unknown");
            Object distanceObj = doc.getMetadata().get("distance");
            double score = 0.88; // Default similarity score fallback
            if (distanceObj instanceof Number num) {
                double distance = num.doubleValue();
                score = Math.max(0.0, Math.min(1.0, 1.0 - distance));
            }
            chunks.add(new ChunkDetail(doc.getFormattedContent(), source, Math.round(score * 100.0) / 100.0));
        }

        String answer = chatClient.prompt()
                .system(sp -> sp.param("context", context))
                .user(userQuery)
                .call()
                .content();

        return new ChatResponse(answer, sources, chunks);
    }

    public Flux<String> askQuestionStream(QueryRequest request) {
        String userQuery = request.query();
        String documentFilter = request.documentFilter();

        SearchRequest.Builder builder = SearchRequest.builder()
                .query(userQuery)
                .topK(TOP_K)
                .similarityThreshold(SIMILARITY_THRESHOLD);

        if (documentFilter != null && !documentFilter.isBlank() && !"ALL".equalsIgnoreCase(documentFilter)) {
            builder.filterExpression("source == '" + documentFilter.replace("'", "") + "'");
        }

        List<Document> relevantDocs = vectorStore.similaritySearch(builder.build());
        String context = relevantDocs.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n\n"));

        return chatClient.prompt()
                .system(sp -> sp.param("context", context))
                .user(userQuery)
                .stream()
                .content();
    }
}
