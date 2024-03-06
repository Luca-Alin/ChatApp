package socialmedia.chatapp.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import socialmedia.chatapp.message.Message;
import socialmedia.chatapp.message.MessageRepository;
import socialmedia.chatapp.user.User;
import socialmedia.chatapp.websocket.mto.MTOType;
import socialmedia.chatapp.websocket.mto.MessageTransferObject;
import socialmedia.chatapp.websocket.mto.models.TypingMessage;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor

@Service
@Slf4j
public class WebSocketService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final MessageRepository messageRepository;

    public String extractUsername(WebSocketSession webSocketSession) {
        URI wsUri = webSocketSession.getUri();
        if (wsUri == null) return null;

        var builder = UriComponentsBuilder.fromUri(wsUri);
        return builder.build().getQueryParams().get("username").getFirst();
    }

    public void closeSession(WebSocketSession webSocketSession, String message) {
        try {
            webSocketSession.sendMessage(new TextMessage(message));
            webSocketSession.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void newConnectionNotification(List<UserSocketCombo> userSocketCombos, UserSocketCombo newConnection) {
        MessageTransferObject mto = MessageTransferObject
                .builder()
                .type(MTOType.CONNECT)
                .content(newConnection.getUser())
                .build();

        // send already connected users a notification that someone new connected
        try {

            String json = mapper.writeValueAsString(mto);
            TextMessage textMessage = new TextMessage(json);

            userSocketCombos.stream()
                    .map(UserSocketCombo::getWss)
                    .filter(wss -> wss != newConnection.getWss()) // don't send the notification to the newly connected user
                    .forEach(wss -> {
                        try {
                            wss.sendMessage(textMessage);
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    });

        } catch (IOException e) {
            log.error(e.getMessage());
        }


        // send newly connected user notifications about all the already connected users
        userSocketCombos.stream()
                .filter(usc -> usc.getWss() != newConnection.getWss()) //don't send notification to the newly connected user
                .map(UserSocketCombo::getUser)
                .forEach(user -> {
                    MessageTransferObject mto2 = MessageTransferObject
                            .builder()
                            .type(MTOType.CONNECT)
                            .content(user)
                            .build();
                    try {
                        String json = mapper.writeValueAsString(mto2);
                        TextMessage message = new TextMessage(json);
                        newConnection.getWss().sendMessage(message);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                });
    }

    public void disconnectNotification(List<UserSocketCombo> userSocketCombos, WebSocketSession session) {
        try {
            User disconnectedUser = userSocketCombos.stream()
                    .filter(usc -> usc.getWss() == session)
                    .findFirst()
                    .orElseThrow()
                    .getUser();

            MessageTransferObject mto = MessageTransferObject
                    .builder()
                    .type(MTOType.DISCONNECT)
                    .content(disconnectedUser)
                    .build();

            String json = mapper.writeValueAsString(mto);
            TextMessage textMessage = new TextMessage(json);


            userSocketCombos.stream()
                    .map(UserSocketCombo::getWss)
                    .filter(wss -> wss != session) // don't send message to disconnected user !!!
                    .forEach(wss -> {
                        try {
                            wss.sendMessage(textMessage);
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    });

        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

    }

    @SuppressWarnings("unchecked")
    public void sendMessage(List<UserSocketCombo> userSocketCombos, MessageTransferObject messageTransferObject) {
        try {
            LinkedHashMap<String, Object> lhm = (LinkedHashMap<String, Object>) messageTransferObject.getContent();
            Message message = mapper.convertValue(lhm, Message.class);

            Message savedMessage = messageRepository.save(message);
            User sender = savedMessage.getSender();
            User receiver = savedMessage.getReceiver();

            MessageTransferObject mto = MessageTransferObject
                    .builder()
                    .type(MTOType.SEND_MESSAGE)
                    .content(savedMessage)
                    .build();
            String json = mapper.writeValueAsString(mto);
            TextMessage textMessage = new TextMessage(json);

            /* filter the userSocketCombos list, get the sender and the receiver of the messages (if they are connected),
            and send 'em the message */
            userSocketCombos.stream()
                    .filter(usc ->
                            sender.getId().equals(usc.getUser().getId()) ||
                            receiver.getId().equals(usc.getUser().getId())
                    )
                    .forEach(usc -> {
                        try {
                            usc.getWss().sendMessage(textMessage);
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void sendTypingNotification(List<UserSocketCombo> userSocketCombos, MessageTransferObject messageTransferObject) {
        try {
            LinkedHashMap<String, Object> lhm = (LinkedHashMap<String, Object>) messageTransferObject.getContent();
            TypingMessage typingMessage = mapper.convertValue(lhm, TypingMessage.class);
            User receiver = typingMessage.getReceiver();

            MessageTransferObject mto = MessageTransferObject
                    .builder()
                    .type(MTOType.TYPING_MESSAGE)
                    .content(typingMessage)
                    .build();
            String json = mapper.writeValueAsString(mto);
            TextMessage textMessage = new TextMessage(json);

            /* find the user under the incidence of the typing notification, then send him the notification
             *  why the foreach? in order to remove the need for the orElseThrow(), orElse(null) etc.
             * */
            userSocketCombos.stream()
                    .filter(usc -> receiver.getId().equals(usc.getUser().getId()))
                    .forEach(usc -> {
                        try {
                            usc.getWss().sendMessage(textMessage);
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
