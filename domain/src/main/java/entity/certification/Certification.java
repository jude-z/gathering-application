package entity.certification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Entity
@Table(name = "certification")
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String certification;

    @Builder
    private Certification(String email, String certification) {
        this.email = email;
        this.certification = certification;
    }

    public static Certification of(String email, String certification) {
        return Certification.builder()
                .email(email)
                .certification(certification)
                .build();
    }
}
