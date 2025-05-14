package com.example.chat.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO para a troca de mensagens via REST ou WebSocket.
 *
 * @param conversationId ID numérico da conversa (grupo ou privada)
 * @param from           identificador do remetente
 * @param to             identificador do destinatário (se privado)
 * @param text           conteúdo da mensagem
 * @param timestamp      momento em que a mensagem foi criada
 */
@Data
@NoArgsConstructor
public class ChatMessageDto {
    private Long conversationId;
    private String from;
    private String to;
    private String text;
    private LocalDateTime timestamp;
}
