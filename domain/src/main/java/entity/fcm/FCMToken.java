package entity.fcm;

import entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "fcm_token")
public class FCMToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "token_value", unique = true)
    private String tokenValue;
    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    private FCMToken(String tokenValue, LocalDate expirationDate, User user) {
        this.tokenValue = tokenValue;
        this.expirationDate = expirationDate;
        this.user = user;
    }

    public void changeExpirationDate(int month){
        expirationDate = LocalDate.now().plusMonths(month);
    }
}
