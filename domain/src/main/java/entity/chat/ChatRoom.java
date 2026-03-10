package entity.chat;

import entity.gathering.Gathering;
import entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
@Getter
@Table(name = "chat_room")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User createdBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id")
    private Gathering gathering;
    private int count;
    public void changeCount(int count){
        this.count = count;
    }

    @Builder
    private ChatRoom(String title, String description, User createdBy, Gathering gathering, int count) {
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.gathering = gathering;
        this.count = count;
    }
}
