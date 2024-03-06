package socialmedia.chatapp.websocket.mto.models;

import lombok.*;
import socialmedia.chatapp.user.User;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypingMessage {
    private User sender;
    private User receiver;
}
