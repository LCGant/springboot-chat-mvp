package com.example.chat.dto;

/**
 * Resposta ao criar ou recuperar uma conversa.
 *
 * @param conversationId ID numérico da conversa gerada ou existente
 */
public record ConversationResponse(Long conversationId) {}
