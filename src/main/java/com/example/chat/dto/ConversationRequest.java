package com.example.chat.dto;

/**
 * Payload para criação ou recuperação de uma conversa privada.
 *
 * @param userA username de um participante
 * @param userB username do outro participante
 */
public record ConversationRequest(String userA, String userB) {}
