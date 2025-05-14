package com.example.chat.websocket;

import com.example.chat.dto.ChatMessageDto;
import com.example.chat.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Gerencia conexões WebSocket para chat:
 * - Sessões de grupos identificadas por ID numérico
 * - Mapeamento de usuários para suporte a mensagens privadas
 */
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper;
    private final ChatService chatService;
    private final Map<Long, Set<WebSocketSession>> groupSessions = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final AtomicLong convCounter = new AtomicLong(1);

    public ChatWebSocketHandler(ObjectMapper mapper, ChatService chatService) {
        this.mapper = mapper;
        this.chatService = chatService;
    }

    /**
     * Ao estabelecer conexão, registra a sessão no grupo e no usuário.
     * Se não houver groupId na query, gera um novo ID de conversa.
     *
     * @param session sessão WebSocket recém-aberta
     */
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        URI uri = session.getUri();
        String query = uri != null ? uri.getQuery() : null;
        String groupStr = null, username = null;
        if (query != null) {
            for (String p : query.split("&")) {
                if (p.startsWith("group=")) groupStr = p.substring(6);
                if (p.startsWith("user="))  username = p.substring(5);
            }
        }
        Long groupId = (groupStr != null)
            ? Long.parseLong(groupStr)
            : convCounter.getAndIncrement();
        groupSessions.computeIfAbsent(groupId, _ -> ConcurrentHashMap.newKeySet())
                     .add(session);
        if (username != null) {
            userSessions.put(username, session);
            session.getAttributes().put("username", username);
        }
        session.getAttributes().put("groupId", groupId);
    }

    /**
     * Retorna a lista de usuários conectados em um grupo específico.
     *
     * @param groupId ID da conversa de grupo
     * @return usernames dos participantes online
     */
    public List<String> getUsersInGroup(Long groupId) {
        return groupSessions.getOrDefault(groupId, Set.of()).stream()
            .map(s -> (String) s.getAttributes().get("username"))
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * Retorna todos os usuários atualmente online no sistema.
     *
     * @return lista de usernames
     */
    public List<String> getAllUsers() {
        return new ArrayList<>(userSessions.keySet());
    }

    /**
     * Processa mensagens recebidas via WebSocket, diferenciando chat de grupo e privado.
     *
     * @param session sessão que enviou a mensagem
     * @param message objeto de texto contendo payload JSON
     * @throws Exception em caso de falha na desserialização ou envio
     */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session,
                                     @NonNull TextMessage message) throws Exception {
        ChatMessageDto msg = mapper.readValue(message.getPayload(), ChatMessageDto.class);

        if (msg.getTo() == null) {
            // Chat em grupo
            Long convId = msg.getConversationId();
            ChatMessageDto saved = chatService.sendMessage(convId, msg);
            Set<WebSocketSession> room = groupSessions.getOrDefault(convId, Set.of());
            TextMessage out = new TextMessage(mapper.writeValueAsString(saved));
            for (WebSocketSession s : room) {
                if (s.isOpen()) s.sendMessage(out);
            }

        } else {
            // Chat privado
            String a = msg.getFrom(), b = msg.getTo();
            Long privateId = chatService.getOrCreateConversation(a, b);
            msg.setConversationId(privateId);
            ChatMessageDto saved = chatService.sendMessage(privateId, msg);
            TextMessage out = new TextMessage(mapper.writeValueAsString(saved));
            WebSocketSession sa = userSessions.get(a);
            WebSocketSession sb = userSessions.get(b);
            if (sa != null && sa.isOpen()) sa.sendMessage(out);
            if (sb != null && sb.isOpen()) sb.sendMessage(out);
        }
    }

    /**
     * Limpa registros ao fechar conexão.
     *
     * @param session sessão encerrada
     * @param status  motivo do fechamento
     */
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session,
                                      @NonNull CloseStatus status) {
        Long groupId = (Long) session.getAttributes().get("groupId");
        Set<WebSocketSession> room = groupSessions.get(groupId);
        if (room != null) {
            room.remove(session);
            if (room.isEmpty()) groupSessions.remove(groupId);
        }
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            userSessions.remove(username);
        }
    }
}
