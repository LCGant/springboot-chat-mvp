package com.example.chat.websocket;

import com.example.chat.dto.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper;
    private final Map<String, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();
    private final AtomicLong convCounter = new AtomicLong(1);

    public ChatWebSocketHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String convId = session.getUri().getQuery();
        if (convId == null) {
            convId = String.valueOf(convCounter.getAndIncrement());
        }
        sessions.computeIfAbsent(convId, id -> ConcurrentHashMap.newKeySet()).add(session);
        session.getAttributes().put("convId", convId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ChatMessageDto msg = mapper.readValue(message.getPayload(), ChatMessageDto.class);
        String convId = msg.getConversationId();
        Set<WebSocketSession> convo = sessions.getOrDefault(convId, Set.of());
        TextMessage out = new TextMessage(mapper.writeValueAsString(msg));
        for (WebSocketSession s : convo) {
            if (s.isOpen()) s.sendMessage(out);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        String convId = (String) session.getAttributes().get("convId");
        Set<WebSocketSession> convo = sessions.get(convId);
        if (convo != null) {
            convo.remove(session);
            if (convo.isEmpty()) sessions.remove(convId);
        }
    }
}
