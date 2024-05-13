package socialmedia.chatapp.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import socialmedia.chatapp.message.Message;
import socialmedia.chatapp.message.MessageRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @PostMapping("")
    public Iterable<UserAndMessageDTO> findAllUsers(@RequestBody User user) {
        User currentUser = userRepository.findByEmail(user.getEmail()).orElseThrow();

        List<User> users = userRepository.findAll().stream().filter(u -> !u.getEmail().equals(user.getEmail())).toList();

        List<UserAndMessageDTO> userAndMessageDTOS = new ArrayList<>();
        users.forEach(u -> {
            List<Message> messages = messageRepository.findConversationsBetweenTwoUsers(currentUser, u);
            userAndMessageDTOS.add(UserAndMessageDTO.builder().user(u).messages(messages).build());
        });
        return userAndMessageDTOS;
    }
}
