package entity.board;

import entity.gathering.Gathering;
import entity.image.Image;
import entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "gathering_id")
    private Gathering gathering;
    @OneToMany(mappedBy = "board")
    private List<Image> images;
    private String title;
    @Lob
    private String description;
    private LocalDateTime registerDate;

    @Builder
    private Board(User user, Gathering gathering, String title, String description, LocalDateTime registerDate) {
        this.user = user;
        this.gathering = gathering;
        this.images = new ArrayList<>();
        this.title = title;
        this.description = description;
        this.registerDate = registerDate;
    }
}
