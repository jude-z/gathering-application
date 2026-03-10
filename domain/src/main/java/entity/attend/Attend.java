package entity.attend;

import entity.meeting.Meeting;
import entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class Attend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User attendBy;
    private LocalDateTime date;

    @Builder
    private Attend(Meeting meeting, User attendBy, LocalDateTime date) {
        this.meeting = meeting;
        this.attendBy = attendBy;
        this.date = date;
    }

}
