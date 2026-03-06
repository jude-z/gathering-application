package dto.querydsl.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import util.ImageUrlProcess;

@AllArgsConstructor
@Builder
@Data
public class ParticipantProjection {
    private Long userId;
    private String username;
    private String nickname;
    private String imageUrl;
    private boolean status;

    public static ParticipantProjection from(ParticipantProjection participantProjection, ImageUrlProcess imageUrlProcess) {
        return ParticipantProjection.builder()
                .userId(participantProjection.getUserId())
                .username(participantProjection.getUsername())
                .nickname(participantProjection.getNickname())
                .imageUrl(imageUrlProcess.convert(participantProjection.getImageUrl()))
                .status(participantProjection.isStatus())
                .build();
    }
}