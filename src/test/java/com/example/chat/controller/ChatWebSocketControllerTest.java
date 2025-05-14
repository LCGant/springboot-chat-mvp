package com.example.chat.controller;

import com.example.chat.dto.ChatMessageDto;
import com.example.chat.dto.ConversationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.chat.service.ChatService;
import com.example.chat.websocket.ChatWebSocketHandler;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatWebSocketController.class)
class ChatWebSocketControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @SuppressWarnings("removal")
    @MockBean
    private ChatService chatService;

    @SuppressWarnings("removal")
    @MockBean
    private ChatWebSocketHandler wsHandler;

    @Test
    void createConversation_returnsConversationId() throws Exception {
        when(chatService.getOrCreateConversation("A", "B")).thenReturn(42L);

        ConversationRequest req = new ConversationRequest("A", "B");
        mvc.perform(post("/conversas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.conversationId").value(42));
    }

    @Test
    void listMessages_returnsEmptyList() throws Exception {
        when(chatService.getMessages(1L)).thenReturn(List.of());

        mvc.perform(get("/conversas/1/mensagens"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }

    @Test
    void createMessage_returnsCreatedMessage() throws Exception {
        when(chatService.sendMessage(eq(1L), any()))
            .thenAnswer(inv -> {
                ChatMessageDto m = inv.getArgument(1);
                m.setConversationId(1L);
                return m;
            });

        mvc.perform(post("/conversas/1/mensagens")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"from\":\"U\",\"text\":\"Hi\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.from").value("U"))
            .andExpect(jsonPath("$.conversationId").value(1));
    }

    @Test
    void listGroupUsers_delegatesToHandler() throws Exception {
        when(wsHandler.getUsersInGroup(1L)).thenReturn(List.of("X","Y"));

        mvc.perform(get("/conversas/1/usuarios"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value("X"))
            .andExpect(jsonPath("$[1]").value("Y"));
    }

    @Test
    void listAllUsers_delegatesToHandler() throws Exception {
        when(wsHandler.getAllUsers()).thenReturn(List.of("A","B"));

        mvc.perform(get("/conversas/usuarios-online"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value("A"))
            .andExpect(jsonPath("$[1]").value("B"));
    }

    @Test
    void getConversationType_returnsCorrectType() throws Exception {
        when(chatService.isPrivateConversation(1L)).thenReturn(true);
        mvc.perform(get("/conversas/1/tipo"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.type").value("private"));

        when(chatService.isPrivateConversation(2L)).thenReturn(false);
        mvc.perform(get("/conversas/2/tipo"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.type").value("group"));
    }
}
