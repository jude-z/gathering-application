package infra.dto.querydsl.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AbleChatRoomProjection {
    private Long id;
    private String chatRoomTitle;
    private String description;
    private int count;
    private String createdBy;
    private String gatheringTitle;
}