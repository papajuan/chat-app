package com.papajuan.chatapp.components;

import com.papajuan.chatapp.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * @author papajuan
 * 04.10.2019
 */
@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(HttpHandshakeInterceptor.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new websocket connection");
    }

    @EventListener
    public void handelWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if(username != null) {
            logger.info("User " + username + " disconnected");
            ChatMessage message = new ChatMessage();
            message.setType(ChatMessage.MessageType.LEAVE);
            message.setSender(username);

            messagingTemplate.convertAndSend("/topic/public", message);
        }
    }

}