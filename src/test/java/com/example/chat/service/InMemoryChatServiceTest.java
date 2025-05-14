package com.example.chat.service;

import com.example.chat.dto.ChatMessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.within;

class InMemoryChatServiceTest {

    private InMemoryChatService service;

    @BeforeEach
    void setUp() {
        service = new InMemoryChatService();
    }

    @Test
    void getOrCreateConversation_samePairSameIdRegardlessOrder() {
        Long id1 = service.getOrCreateConversation("Alice", "Bob");
        Long id2 = service.getOrCreateConversation("Bob", "Alice");
        assertThat(id1).isEqualTo(id2).isPositive();
    }

    @Test
    void sendMessage_and_getMessages() {
        Long convId = service.getOrCreateConversation("User1", "User2");
        ChatMessageDto msg = new ChatMessageDto();
        msg.setFrom("User1");
        msg.setText("Hello");
        msg.setConversationId(convId);

        ChatMessageDto saved = service.sendMessage(convId, msg);
        // timestamp atualizado e comparado com tolerância de 1 segundo
        assertThat(saved.getTimestamp())
          .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));

        List<ChatMessageDto> list = service.getMessages(convId);
        assertThat(list).hasSize(1).contains(saved);
    }

    @Test
    void getMessages_emptyForUnknownConversation() {
        assertThat(service.getMessages(999L)).isEmpty();
    }

    @Test
    void isPrivateConversation_trueAfterCreation() {
        Long id = service.getOrCreateConversation("A", "B");
        assertThat(service.isPrivateConversation(id)).isTrue();
        assertThat(service.isPrivateConversation(id + 1)).isFalse();
    }
}
