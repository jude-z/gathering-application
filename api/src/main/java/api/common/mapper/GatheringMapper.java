package api.common.mapper;

import infra.dto.jdbc.gathering.GatheringDetailProjection;
import entity.category.Category;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.user.User;
import util.ImageUrlProcess;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static api.requeset.gathering.GatheringRequestDto.*;
import static api.response.gathering.GatheringResponseDto.*;

public class GatheringMapper {

    public static Gathering toGathering(AddGatheringRequest request, User createBy, Image image, Category category){
        return Gathering.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .createBy(createBy)
                .registerDate(LocalDateTime.now())
                .gatheringImage(image)
                .category(category)
                .count(1)
                .build();
    }

    public static void updateGathering(Gathering gathering, UpdateGatheringRequest request, Image image){
        gathering.change(request.getTitle(), request.getContent(), image);
    }

    public static GatheringResponse toGatheringResponse(List<GatheringDetailProjection> gatheringDetailProjectionList, boolean hasNext,
                                                        ImageUrlProcess imageUrlProcess) {
        return GatheringResponse.builder()
                .title(gatheringDetailProjectionList.getFirst().getTitle())
                .hasNext(hasNext)
                .content(gatheringDetailProjectionList.getFirst().getContent())
                .registerDate(gatheringDetailProjectionList.getFirst().getRegisterDate())
                .category(gatheringDetailProjectionList.getFirst().getCategory())
                .createdBy(gatheringDetailProjectionList.getFirst().getCreatedBy())
                .createdByUrl(gatheringDetailProjectionList.getFirst().getCreatedByUrl())
                .imageUrl(imageUrlProcess.convert(gatheringDetailProjectionList.getFirst().getUrl()))
                .count(gatheringDetailProjectionList.getFirst().getCount())
                .participatedByList(new ArrayList<>())
                .build();
    }
}
