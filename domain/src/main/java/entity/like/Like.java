package entity.like;

import entity.gathering.Gathering;
import entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User likedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id")
    private Gathering gathering;

    @Builder
    private Like(User likedBy, Gathering gathering) {
        this.likedBy = likedBy;
        this.gathering = gathering;
    }

    public static Like of(Gathering gathering, User user) {
        return Like.builder()
                .gathering(gathering)
                .likedBy(user)
                .build();
    }
}
