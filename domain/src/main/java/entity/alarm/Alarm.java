package entity.alarm;


import entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.*;

@Getter
@NoArgsConstructor
@Entity
public class Alarm {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String content;
    private LocalDateTime date;
    private boolean checked;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void setChecked(){
        this.checked = true;
    }
    @Builder
    private Alarm(String content, User user, LocalDateTime date, boolean checked){
        this.content = content;
        this.user = user;
        this.date = date;
        this.checked = checked;
    }

    public static Alarm from(String content,User user){
        return Alarm.builder()
                .content(content)
                .user(user)
                .date(LocalDateTime.now())
                .checked(false)
                .build();
    }

}
