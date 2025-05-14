package com.example.chat.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatMessage {
    private String conversationId;
    private String from;
    private String text;
    private LocalDateTime timestamp = LocalDateTime.now();
}
