package entity.enrollment;

import entity.gathering.Gathering;
import entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "enrollment")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean accepted;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id")
    private Gathering gathering;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User enrolledBy;
    private LocalDateTime date;

    @Builder
    public Enrollment(boolean accepted, Gathering gathering, User enrolledBy, LocalDateTime date) {
        this.accepted = accepted;
        this.gathering = gathering;
        this.enrolledBy = enrolledBy;
        this.date = date;
    }

    public void changeAccepted(){
        this.accepted = true;
    }

}
