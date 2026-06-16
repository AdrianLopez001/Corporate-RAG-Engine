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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock private VectorStore vectorStore;
    @Mock private ChatClient chatClient;
    @Mock private ChatClient.ChatClientRequestSpec requestSpec;
    @Mock private ChatClient.CallResponseSpec callResponseSpec;

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        chatService = new ChatService(vectorStore, chatClient);
    }

    private void stubChatClient(String answer) {
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(any())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(answer);
    }

    @Test
    void shouldReturnAnswerWithSources() {
        Document doc = new Document("Relevant document content.", Map.of("source", "manual.pdf"));
        when(vectorStore.similaritySearch(any())).thenReturn(List.of(doc));
        stubChatClient("The answer based on the manual.");

        ChatResponse response = chatService.askQuestion("What is in the manual?");

        assertThat(response.answer()).isEqualTo("The answer based on the manual.");
        assertThat(response.sources()).containsExactly("manual.pdf");
    }

    @Test
    void shouldDeduplicateSources() {
        Document doc1 = new Document("First chunk.", Map.of("source", "report.pdf"));
        Document doc2 = new Document("Second chunk.", Map.of("source", "report.pdf"));
        when(vectorStore.similaritySearch(any())).thenReturn(List.of(doc1, doc2));
        stubChatClient("Answer from two chunks of the same file.");

        ChatResponse response = chatService.askQuestion("Summarize the report.");

        assertThat(response.sources()).hasSize(1).containsExactly("report.pdf");
    }

    @Test
    void shouldReturnEmptySourcesWhenNoDocumentsFound() {
        when(vectorStore.similaritySearch(any())).thenReturn(List.of());
        stubChatClient("I do not have information on that topic.");

        ChatResponse response = chatService.askQuestion("Unrelated question");

        assertThat(response.sources()).isEmpty();
        assertThat(response.answer()).contains("I do not have information");
    }
}
