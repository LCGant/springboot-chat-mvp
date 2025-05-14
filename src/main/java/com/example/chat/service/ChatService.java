package com.example.chat.service;

import com.example.chat.dto.ChatMessageDto;
import java.util.List;

public interface ChatService {
    ChatMessageDto sendMessage(String conversationId, ChatMessageDto message);
    List<ChatMessageDto> getMessages(String conversationId);
}
