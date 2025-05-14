package com.example.chat.service;

import com.example.chat.dto.ChatMessageDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryChatService implements ChatService {
    private final Map<String, List<ChatMessageDto>> storage = new ConcurrentHashMap<>();

    @Override
    public ChatMessageDto sendMessage(String conversationId, ChatMessageDto message) {
        message.setTimestamp(LocalDateTime.now());
        storage.putIfAbsent(conversationId, new ArrayList<>());
        storage.get(conversationId).add(message);
        return message;
    }

    @Override
    public List<ChatMessageDto> getMessages(String conversationId) {
        return Collections.unmodifiableList(
            storage.getOrDefault(conversationId, Collections.emptyList())
        );
    }
}
