package com.example.chat.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatMessageDto {
    private String conversationId;
    private String from;
    private String text;
    private LocalDateTime timestamp;
}
