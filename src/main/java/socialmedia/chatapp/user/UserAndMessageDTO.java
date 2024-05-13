package socialmedia.chatapp.user;

import lombok.*;
import socialmedia.chatapp.message.Message;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAndMessageDTO {
    private User user;
    private List<Message> messages;
}
