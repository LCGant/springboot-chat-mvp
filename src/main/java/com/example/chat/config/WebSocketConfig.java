package com.example.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.example.chat.websocket.ChatWebSocketHandler;

/**
 * Configura o endpoint WebSocket para o módulo de chat.
 * <p>
 * - Habilita suporte a WebSocket com @EnableWebSocket
 * - Registra o handler no caminho "/ws/chat"
 * - Permite conexões de qualquer origem (CORS "*")
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler handler;

    /**
     * Construtor que injeta o handler responsável pela lógica de WebSocket.
     *
     * @param handler componente que gerencia sessões e mensagens via WebSocket
     */
    public WebSocketConfig(ChatWebSocketHandler handler) {
        this.handler = handler;
    }

    /**
     * Registra o {@link ChatWebSocketHandler} no endpoint "/ws/chat"
     * e libera CORS para permitir conexões de quaisquer origens.
     *
     * @param registry objeto para registrar handlers de WebSocket
     */
    @SuppressWarnings("null")
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/chat")
                .setAllowedOrigins("*");
    }
}