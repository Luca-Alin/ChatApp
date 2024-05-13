package socialmedia.chatapp.websocket;

import lombok.*;
import org.springframework.web.socket.WebSocketSession;
import socialmedia.chatapp.user.User;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserSocketCombo {
    private User user;
    private WebSocketSession wss;
}
