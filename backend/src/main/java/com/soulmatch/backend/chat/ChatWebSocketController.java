
package com.soulmatch.backend.chat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.security.Principal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@Validated
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(
            ChatService chatService,
            SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Recibe un mensaje enviado por WebSocket y lo distribuye
     * a los participantes de la conversación.
     *
     * Destino de entrada:
     * /app/chat/{conversationId}/send
     *
     * Destino de salida:
     * /topic/chat/{conversationId}
     */
    @MessageMapping("/chat/{conversationId}/send")
    public void sendMessage(
            @DestinationVariable UUID conversationId,
            @Valid @Payload ChatMessageRequest request,
            Principal principal) {

        if (principal == null || principal.getName() == null
                || principal.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Debes iniciar sesión para enviar mensajes.");
        }

        if (request == null || request.content() == null
                || request.content().isBlank()) {
            throw new IllegalArgumentException(
                    "El contenido del mensaje es obligatorio.");
        }

        /*
         * ChatService debe obtener al usuario autenticado desde
         * Spring Security y verificar que participa en la conversación.
         */
        ChatController.SendMessageRequest sendRequest =
                new ChatController.SendMessageRequest(request.content());

        ChatController.MessageResponse savedMessage =
                chatService.sendMessage(conversationId, sendRequest);

        ChatMessageResponse response = new ChatMessageResponse(
                savedMessage.id(),
                savedMessage.conversationId(),
                savedMessage.senderId(),
                savedMessage.content(),
                savedMessage.createdAt()
        );

        messagingTemplate.convertAndSend(
                "/topic/chat/" + conversationId,
                response
        );
    }

    /**
     * DTO de entrada para mensajes enviados por WebSocket.
     */
    public record ChatMessageRequest(
            @NotBlank(message = "El mensaje no puede estar vacío")
            @Size(max = 5000, message = "El mensaje no puede superar los 5000 caracteres")
            String content
    ) {
    }

    /**
     * DTO publicado a los clientes suscritos a la conversación.
     */
    public record ChatMessageResponse(
            UUID id,
            UUID conversationId,
            UUID senderId,
            String content,
            Instant createdAt
    ) {
    }
}
