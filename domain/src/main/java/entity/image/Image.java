package entity.image;

import entity.board.Board;
import entity.gathering.Gathering;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Table(name = "image")
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String url;
    @Column(name = "content_type")
    private String contentType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id")
    private Gathering gathering;

    @Builder
    public Image(String url, String contentType, Board board, Gathering gathering) {
        this.url = url;
        this.contentType = contentType;
        this.board = board;
        this.gathering = gathering;
    }
}
