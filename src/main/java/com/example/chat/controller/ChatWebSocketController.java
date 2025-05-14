package com.example.chat.controller;

import com.example.chat.dto.ChatMessageDto;
import com.example.chat.dto.ConversationRequest;
import com.example.chat.dto.ConversationResponse;
import com.example.chat.service.ChatService;
import com.example.chat.websocket.ChatWebSocketHandler;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Endpoints REST para gerenciamento de saúde, conversas e mensagens de chat.
 */
@RestController
@RequestMapping("/conversas")
public class ChatWebSocketController {

    private final ChatService chatService;
    private final ChatWebSocketHandler wsHandler;

    public ChatWebSocketController(ChatService chatService,
                                   ChatWebSocketHandler wsHandler) {
        this.chatService = chatService;
        this.wsHandler   = wsHandler;
    }

    /**
     * Cria ou retorna um ID numérico para conversa entre dois usuários.
     *
     * @param req informações dos usuários participantes
     * @return objeto contendo o ID da conversa
     */
    @PostMapping
    public ConversationResponse createConversation(@RequestBody ConversationRequest req) {
        Long id = chatService.getOrCreateConversation(req.userA(), req.userB());
        return new ConversationResponse(id);
    }

    /**
     * Retorna o histórico de mensagens de uma conversa.
     *
     * @param conversationId identificador da conversa
     * @return lista de mensagens trocadas
     */
    @GetMapping("/{conversationId}/mensagens")
    public List<ChatMessageDto> listMessages(@PathVariable Long conversationId) {
        return chatService.getMessages(conversationId);
    }

    /**
     * Envia uma nova mensagem para a conversa (fallback via REST).
     *
     * @param conversationId identificador da conversa
     * @param message objeto de mensagem com conteúdo e remetente
     * @return a mensagem enviada com timestamp preenchido
     */
    @PostMapping("/{conversationId}/mensagens")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessageDto createMessage(@PathVariable Long conversationId,
                                        @RequestBody ChatMessageDto message) {
        message.setConversationId(conversationId);
        return chatService.sendMessage(conversationId, message);
    }

    /**
     * Lista os usuários atualmente conectados em uma conversa em grupo.
     *
     * @param conversationId identificador da conversa de grupo
     * @return lista de usernames online nessa conversa
     */
    @GetMapping("/{conversationId}/usuarios")
    public List<String> listGroupUsers(@PathVariable Long conversationId) {
        return wsHandler.getUsersInGroup(conversationId);
    }

    /**
     * Lista todos os usuários atualmente online no sistema.
     *
     * @return lista de usernames online
     */
    @GetMapping("/usuarios-online")
    public List<String> listAllUsers() {
        return wsHandler.getAllUsers();
    }

    /**
     * Informa se a conversa é privada ou em grupo.
     *
     * @param conversationId identificador da conversa
     * @return mapa com chave "type" e valor "private" ou "group"
     */
    @GetMapping("/{conversationId}/tipo")
    public Map<String, String> getConversationType(@PathVariable Long conversationId) {
        boolean priv = chatService.isPrivateConversation(conversationId);
        return Collections.singletonMap("type", priv ? "private" : "group");
    }
}