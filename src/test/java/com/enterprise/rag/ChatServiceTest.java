package com.enterprise.rag;

import com.enterprise.rag.dto.ChatResponse;
import com.enterprise.rag.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.ChatClientRequestSpec.SystemSpec systemSpec;

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        chatService = new ChatService(vectorStore, chatClient);
    }

    @Test
    void shouldReturnAnswerWithSources() {
        Document doc = new Document("Conteúdo relevante do documento.", Map.of("source", "manual.pdf"));
        when(vectorStore.similaritySearch(any())).thenReturn(List.of(doc));

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(any())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("Resposta gerada pelo modelo.");

        ChatResponse response = chatService.askQuestion("O que está no manual?");

        assertThat(response.answer()).isEqualTo("Resposta gerada pelo modelo.");
        assertThat(response.sources()).containsExactly("manual.pdf");
    }

    @Test
    void shouldHandleNoRelevantDocuments() {
        when(vectorStore.similaritySearch(any())).thenReturn(List.of());

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(any())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("Não encontrei informações sobre esse tema.");

        ChatResponse response = chatService.askQuestion("Pergunta sem contexto");

        assertThat(response.answer()).contains("Não encontrei");
        assertThat(response.sources()).isEmpty();
    }
}
