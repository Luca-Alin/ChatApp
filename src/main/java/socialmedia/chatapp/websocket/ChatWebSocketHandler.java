package socialmedia.chatapp.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import socialmedia.chatapp.user.User;
import socialmedia.chatapp.user.UserRepository;
import socialmedia.chatapp.websocket.mto.MTOType;
import socialmedia.chatapp.websocket.mto.MessageTransferObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor

@Component
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final UserRepository userRepository;
    private final WebSocketService webSocketService;

    private final ObjectMapper mapper = new ObjectMapper();
    private final List<UserSocketCombo> userSocketCombos = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        String username = webSocketService.extractUsername(session);
        if (username == null) {
            webSocketService.closeSession(session, "usernameisnull");
            return;
        }

        if (userSocketCombos.stream().anyMatch(usc -> username.equals(usc.getUser().getEmail()))) {
            webSocketService.closeSession(session, "useralreadyconnected");
            return;
        }

        User user = userRepository.findByEmail(username).orElse(null);
        if (user == null) {
            webSocketService.closeSession(session, "usernotfound");
            return;
        }

        UserSocketCombo usc = UserSocketCombo.builder().wss(session).user(user).build();
        userSocketCombos.add(usc);

        webSocketService.newConnectionNotification(userSocketCombos, usc);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        webSocketService.disconnectNotification(userSocketCombos, session);
        userSocketCombos.removeIf(usc -> usc.getWss() == session);
    }


    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            MessageTransferObject mto = mapper.readValue((String) message.getPayload(), MessageTransferObject.class);

            switch (mto.getType()) {
                case MTOType.SEND_MESSAGE -> webSocketService.sendMessage(userSocketCombos, mto);
                case MTOType.TYPING_MESSAGE -> webSocketService.sendTypingNotification(userSocketCombos, mto);
                default -> log.error("case {} does not exists", mto.getType());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


}

