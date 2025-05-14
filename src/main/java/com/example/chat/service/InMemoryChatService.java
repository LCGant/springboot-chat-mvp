package com.example.chat.service;

import com.example.chat.dto.ChatMessageDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementação em memória de {@link ChatService}.
 * Usa mapas concorrentes para armazenar conversas e mensagens sem persistência externa.
 */
@Service
public class InMemoryChatService implements ChatService {

    private final AtomicLong conversationCounter = new AtomicLong(1);
    private final Map<String, Long> conversationIds = new ConcurrentHashMap<>();
    private final Map<Long, List<ChatMessageDto>> storage = new ConcurrentHashMap<>();

    @Override
    public Long getOrCreateConversation(String userA, String userB) {
        List<String> pair = Arrays.asList(userA, userB);
        Collections.sort(pair);
        String key = pair.get(0) + "_" + pair.get(1);
        return conversationIds.computeIfAbsent(key,
            _ -> conversationCounter.getAndIncrement()
        );
    }

    @Override
    public ChatMessageDto sendMessage(Long conversationId, ChatMessageDto message) {
        message.setTimestamp(LocalDateTime.now());
        storage.putIfAbsent(conversationId, new ArrayList<>());
        storage.get(conversationId).add(message);
        return message;
    }

    @Override
    public List<ChatMessageDto> getMessages(Long conversationId) {
        return Collections.unmodifiableList(
            storage.getOrDefault(conversationId, Collections.emptyList())
        );
    }

    @Override
    public boolean isPrivateConversation(Long conversationId) {
        return conversationIds.containsValue(conversationId);
    }
}
