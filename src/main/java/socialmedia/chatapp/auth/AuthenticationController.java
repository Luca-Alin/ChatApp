package socialmedia.chatapp.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import socialmedia.chatapp.auth.model.LoginRequest;
import socialmedia.chatapp.user.User;
import socialmedia.chatapp.user.UserRepository;

@RequiredArgsConstructor

@RestController
@RequestMapping("api/v1/auth")
public class AuthenticationController {
    private final UserRepository userRepository;


    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        var user = userRepository.findByEmail(loginRequest.getEmail());

        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }
}
