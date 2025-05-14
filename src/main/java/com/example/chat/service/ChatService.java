package com.example.chat.service;

import com.example.chat.dto.ChatMessageDto;
import java.util.List;

/**
 * Serviço de chat responsável por conversas e mensagens.
 */
public interface ChatService {

    /**
     * Cria (ou retorna) um ID numérico único para a conversa entre dois usuários.
     *
     * @param userA username do primeiro participante
     * @param userB username do segundo participante
     * @return ID da conversa (numeric)
     */
    Long getOrCreateConversation(String userA, String userB);

    /**
     * Persiste e retorna a mensagem enviada em uma conversa.
     *
     * @param conversationId ID da conversa onde a mensagem será inserida
     * @param message        DTO contendo remetente, texto e opcional destinatário
     * @return mesma DTO com timestamp preenchido
     */
    ChatMessageDto sendMessage(Long conversationId, ChatMessageDto message);

    /**
     * Recupera todo o histórico de mensagens de uma conversa.
     *
     * @param conversationId ID da conversa
     * @return lista de mensagens em ordem de envio
     */
    List<ChatMessageDto> getMessages(Long conversationId);

    /**
     * Indica se determinada conversa foi criada como privada (entre dois usuários).
     *
     * @param conversationId ID da conversa
     * @return {@code true} se privada, {@code false} se é conversa de grupo
     */
    boolean isPrivateConversation(Long conversationId);
}
