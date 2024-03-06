package socialmedia.chatapp.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import socialmedia.chatapp.user.User;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query("""
            select m
            from Message m
            where (m.sender = ?1 and m.receiver = ?2)
            or (m.sender = ?2 and m.receiver = ?1)
            """)
    List<Message> findConversationsBetweenTwoUsers(User user1, User user2);
}
