package com.example.chat.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Modelo de domínio para mensagens de chat mantidas em memória.
 * Cada instância representa uma única mensagem com marcação de tempo.
 */
@Data
@NoArgsConstructor
public class ChatMessage {

    /** Identificador numérico da conversa (grupo ou privada). */
    private String conversationId;

    /** Username de quem enviou a mensagem. */
    private String from;

    /** Conteúdo textual da mensagem. */
    private String text;

    /** Timestamp de criação, inicializado na construção. */
    private LocalDateTime timestamp = LocalDateTime.now();
}
