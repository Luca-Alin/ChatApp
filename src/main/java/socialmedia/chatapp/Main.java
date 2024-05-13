package socialmedia.chatapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javafaker.Faker;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import socialmedia.chatapp.message.Message;
import socialmedia.chatapp.message.MessageRepository;
import socialmedia.chatapp.user.User;
import socialmedia.chatapp.user.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner(
            final UserRepository userRepository,
            final MessageRepository messageRepository
            ) {
        return args -> {

            User user1 = User
                    .builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("johndoe@gmail.com")
                    .password("password")
                    .build();
            User user2 = User
                    .builder()
                    .firstName("Jane")
                    .lastName("Doe")
                    .email("janedoe@gmail.com")
                    .password("password")
                    .build();
            User user3 = User
                    .builder()
                    .firstName("John")
                    .lastName("Smith")
                    .email("johnsmith@gmail.com")
                    .password("password")
                    .build();
            User user4 = User
                    .builder()
                    .firstName("Jane")
                    .lastName("Smith")
                    .email("janesmith@gmail.com")
                    .password("password")
                    .build();


            List<User> users = userRepository.saveAll(List.of(user1, user2, user3, user4));

            Random random = new Random();
            for (int i = 0; i < 100; i++) {
                User userA = users.get(random.nextInt(users.size()));
                User userB = users.get(random.nextInt(users.size()));
                if (Objects.equals(userA.getId(), userB.getId()))
                    continue;

                Message m = Message
                        .builder()
                        .sender(userA)
                        .receiver(userB)
                        .textMessage(Faker.instance().shakespeare().asYouLikeItQuote())
                        .build();
                messageRepository.save(m);
            }
        };
    }


}

