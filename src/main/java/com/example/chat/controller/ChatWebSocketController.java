// src/main/java/com/example/chat/controller/ChatWebSocketController.java
package com.example.chat.controller;

import com.example.chat.dto.ChatMessageDto;
import com.example.chat.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversas/{conversationId}/mensagens")
public class ChatWebSocketController {

    private final ChatService chatService;

    public ChatWebSocketController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public List<ChatMessageDto> list(@PathVariable String conversationId) {
        return chatService.getMessages(conversationId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessageDto create(
            @PathVariable String conversationId,
            @RequestBody ChatMessageDto message
    ) {
        message.setConversationId(conversationId);
        return chatService.sendMessage(conversationId, message);
    }
}
