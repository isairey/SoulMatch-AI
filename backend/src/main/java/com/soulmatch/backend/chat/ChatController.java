
package com.soulmatch.backend.chat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Obtiene las conversaciones del usuario autenticado.
     */
    @GetMapping("/conversations")
    public List<ConversationResponse> getMyConversations() {
        return chatService.getMyConversations();
    }

    /**
     * Obtiene una conversación específica.
     */
    @GetMapping("/conversations/{conversationId}")
    public ConversationResponse getConversation(
            @PathVariable UUID conversationId) {
        return chatService.getConversationById(conversationId);
    }

    /**
     * Obtiene los mensajes de una conversación.
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public List<MessageResponse> getMessages(
            @PathVariable UUID conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        return chatService.getMessages(conversationId, page, size);
    }

    /**
     * Envía un mensaje a una conversación.
     */
    @PostMapping("/conversations/{conversationId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse sendMessage(
            @PathVariable UUID conversationId,
            @Valid @RequestBody SendMessageRequest request) {

        return chatService.sendMessage(conversationId, request);
    }

    /**
     * Marca los mensajes de una conversación como leídos.
     */
    @PostMapping("/conversations/{conversationId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markMessagesAsRead(
            @PathVariable UUID conversationId) {

        chatService.markMessagesAsRead(conversationId);
    }

    /**
     * Elimina un mensaje propio, si las reglas de negocio lo permiten.
     */
    @DeleteMapping("/messages/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@PathVariable UUID messageId) {
        chatService.deleteMessage(messageId);
    }

    /**
     * DTO para enviar un mensaje.
     */
    public record SendMessageRequest(
            @NotBlank(message = "El contenido del mensaje es obligatorio")
            @Size(max = 5000, message = "El mensaje no puede superar los 5000 caracteres")
            String content
    ) {
    }

    /**
     * DTO de respuesta de una conversación.
     */
    public record ConversationResponse(
            UUID id,
            UUID matchId,
            UUID participantOneId,
            UUID participantTwoId,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    /**
     * DTO de respuesta de un mensaje.
     */
    public record MessageResponse(
            UUID id,
            UUID conversationId,
            UUID senderId,
            String content,
            Instant createdAt,
            Instant readAt
    ) {
    }
}
