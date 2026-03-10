package entity.gathering;


import entity.category.Category;
import entity.enrollment.Enrollment;
import entity.fcm.Topic;
import entity.image.Image;
import entity.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Getter
@NoArgsConstructor
@Entity
@Table(name = "gathering")
public class Gathering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Lob
    private String content;
    private LocalDateTime registerDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User createBy;

    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image gatheringImage;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "gathering")
    private List<Enrollment> enrollments;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Builder
    private Gathering(String title, String content, LocalDateTime registerDate, User createBy, int count,
                     Image gatheringImage, Category category, Topic topic) {
        this.title = title;
        this.content = content;
        this.registerDate = registerDate;
        this.createBy = createBy;
        this.count = count;
        this.gatheringImage = gatheringImage;
        this.category = category;
        this.enrollments = new ArrayList<>();
        this.topic = topic;
    }
    public void changeTopic(Topic topic){
        this.topic = topic;
    }

    public void change(String title, String content, Image gatheringImage){
        this.title = title;
        this.content = content;
        if(gatheringImage != null) this.gatheringImage = gatheringImage;
        this.registerDate = LocalDateTime.now();
    }

}
