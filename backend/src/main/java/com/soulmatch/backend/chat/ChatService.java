
package com.soulmatch.backend.chat;

import com.soulmatch.backend.chat.ChatController.ConversationResponse;
import com.soulmatch.backend.chat.ChatController.MessageResponse;
import com.soulmatch.backend.chat.ChatController.SendMessageRequest;
import com.soulmatch.backend.users.User;
import com.soulmatch.backend.users.UserRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ChatService {

    private static final int MAX_PAGE_SIZE = 100;

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public ChatService(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> getMyConversations() {
        User user = getAuthenticatedUser();

        return conversationRepository
                .findAllByParticipantOne_IdOrParticipantTwo_IdOrderByUpdatedAtDesc(
                        user.getId(), user.getId())
                .stream()
                .map(this::toConversationResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConversationResponse getConversationById(UUID conversationId) {
        User user = getAuthenticatedUser();
        Conversation conversation = getConversationForParticipant(
                conversationId, user.getId());

        return toConversationResponse(conversation);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(
            UUID conversationId, int page, int size) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "El número de página no puede ser negativo.");
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "El tamaño de página debe estar entre 1 y "
                            + MAX_PAGE_SIZE + ".");
        }

        User user = getAuthenticatedUser();
        getConversationForParticipant(conversationId, user.getId());

        Pageable pageable = PageRequest.of(page, size);

        return messageRepository
                .findAllByConversation_IdOrderByCreatedAtDesc(
                        conversationId, pageable)
                .getContent()
                .stream()
                .map(this::toMessageResponse)
                .toList();
    }

    public MessageResponse sendMessage(
            UUID conversationId, SendMessageRequest request) {

        if (request == null || request.content() == null
                || request.content().isBlank()) {
            throw new IllegalArgumentException(
                    "El contenido del mensaje es obligatorio.");
        }

        String content = request.content().trim();

        if (content.length() > 5000) {
            throw new IllegalArgumentException(
                    "El mensaje no puede superar los 5000 caracteres.");
        }

        User sender = getAuthenticatedUser();

        Conversation conversation = getConversationForParticipant(
                conversationId, sender.getId());

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(content);
        message.setCreatedAt(Instant.now());

        Message savedMessage = messageRepository.save(message);

        conversation.setUpdatedAt(Instant.now());
        conversationRepository.save(conversation);

        return toMessageResponse(savedMessage);
    }

    public void markMessagesAsRead(UUID conversationId) {
        User user = getAuthenticatedUser();

        getConversationForParticipant(conversationId, user.getId());

        messageRepository.markMessagesAsRead(
                conversationId, user.getId(), Instant.now());
    }

    public void deleteMessage(UUID messageId) {
        User user = getAuthenticatedUser();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ChatNotFoundException(
                        "No se encontró el mensaje solicitado."));

        if (message.getSender() == null
                || !message.getSender().getId().equals(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Solo puedes eliminar tus propios mensajes.");
        }

        messageRepository.delete(message);
    }

    private Conversation getConversationForParticipant(
            UUID conversationId, UUID userId) {

        Conversation conversation = conversationRepository
                .findById(conversationId)
                .orElseThrow(() -> new ChatNotFoundException(
                        "No se encontró la conversación solicitada."));

        boolean isParticipant =
                conversation.getParticipantOne() != null
                        && conversation.getParticipantOne().getId()
                                .equals(userId)
                || conversation.getParticipantTwo() != null
                        && conversation.getParticipantTwo().getId()
                                .equals(userId);

        if (!isParticipant) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No tienes permiso para acceder a esta conversación.");
        }

        return conversation;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()
                || "anonymousUser".equals(authentication.getName())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Debes iniciar sesión para realizar esta operación.");
        }

        return userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ChatNotFoundException(
                        "No se encontró el usuario autenticado."));
    }

    private ConversationResponse toConversationResponse(
            Conversation conversation) {

        return new ConversationResponse(
                conversation.getId(),
                conversation.getMatch() != null
                        ? conversation.getMatch().getId() : null,
                conversation.getParticipantOne().getId(),
                conversation.getParticipantTwo().getId(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }

    private MessageResponse toMessageResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getConversation().getId(),
                message.getSender().getId(),
                message.getContent(),
                message.getCreatedAt(),
                message.getReadAt()
        );
    }
}
