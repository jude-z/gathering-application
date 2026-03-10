package entity.user;

import entity.fcm.FCMToken;
import entity.image.Image;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
@Table(name="user")
@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String address;
    private Integer age;
    private String hobby;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String nickname;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image profileImage;
    @OneToMany(mappedBy = "user")
    private List<FCMToken> tokens;
    private String refreshToken;

    public void changeProfileImage(Image profileImage){
        this.profileImage = profileImage;
    }

    public void change(String password, String email, String address, Integer age, String hobby, String nickname){
        this.password = password;
        this.email = email;
        this.address = address;
        this.age = age;
        this.hobby = hobby;
        this.nickname = nickname;
    }

    @Builder
    private User(String username, String password, String email, String address, Integer age, String hobby,
                 Role role, String nickname, Image profileImage, String refreshToken) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.address = address;
        this.age = age;
        this.hobby = hobby;
        this.role = role;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.tokens = new ArrayList<>();
        this.refreshToken = refreshToken;
    }

}
