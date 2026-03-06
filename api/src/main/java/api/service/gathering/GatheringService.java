package api.service.gathering;

import api.response.ApiResponse;
import api.service.fcm.FCMTokenTopicService;
import api.service.image.ImageUploadService;
import api.service.recommend.RecommendService;
import jpa.repository.category.CategoryRepository;
import jpa.repository.enrollment.EnrollmentRepository;
import jpa.repository.fcm.TopicRepository;
import jpa.repository.gathering.GatheringRepository;
import jpa.repository.image.ImageRepository;
import jpa.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class GatheringService {

    private final GatheringRepository gatheringRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ImageRepository imageRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final ImageUploadService imageUploadService;
    private final RecommendService recommendService;
    private final FCMTokenTopicService fcmTokenTopicService;

    public ApiResponse addGathering(AddGatheringRequest addGatheringRequest, MultipartFile file, Long userId) throws IOException {

            User user = userRepository.findById(userId)
                    .orElseThrow(()->new NotFoundUserException("no exist User!!"));
            if(!CategoryUtil.existCategory(addGatheringRequest.getCategory())){
                throw new NotFoundCategoryException("category not found");
            }
            Image image = null;
            image = saveImage(image,file);
            Category category = Category.from(addGatheringRequest.getCategory());
            Gathering gathering = Gathering.of(addGatheringRequest,user,image,category);
            Enrollment enrollment= Enrollment.of(true, gathering, user, LocalDateTime.now());
            if(image!=null) imageRepository.save(image);
            Topic topic = generateTopic(gathering);
            gathering.changeTopic(topic);
            gatheringRepository.save(gathering);
            categoryRepository.save(category);
            topicRepository.save(topic);
            enrollmentRepository.save(enrollment);
            fcmTokenTopicService.subscribeToTopic(topic.getTopicName(),userId);
            recommendService.addScore(gathering.getId(), 1);
            return AddGatheringResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE, gathering.getId());
    }

    public ApiResponse updateGathering(UpdateGatheringRequest updateGatheringRequest, MultipartFile file, Long userId, Long gatheringId) throws IOException {

            userRepository.findById(userId)
                    .orElseThrow(()->new NotFoundUserException("no exist User!!"));
            Category category = categoryRepository.findBy(gatheringId, updateGatheringRequest.getCategory())
                    .orElseThrow(()-> new NotFoundCategoryException("category not found"));
            Gathering gathering = gatheringRepository.findGatheringFetchCreatedByAndTokensId(gatheringId)
                    .orElseThrow(()->new NotFoundGatheringException("no exist Gathering!!"));
            User createBy = gathering.getCreateBy();
            boolean authorize = ObjectUtils.nullSafeEquals(createBy.getId(),userId);
            if(!authorize) throw new NotAuthorizeException("no authorize!!");
            Image image = null;
            image = saveImage(image,file);
            if(image!=null) imageRepository.save(image);
            gathering.changeGathering(image,updateGatheringRequest);
            category.changeName(updateGatheringRequest.getCategory());
            return UpdateGatheringResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE, gatheringId);
    }

    public ApiResponse gatheringDetail(Long gatheringId){

            List<GatheringDetailQuery> gatheringDetailQueries = gatheringRepository.gatheringDetail(gatheringId);
            if(gatheringDetailQueries.isEmpty()) throw new NotFoundGatheringException("no exist Gathering!!!");
            recommendService.addScore(gatheringId,1);
            return getGatheringResponse(gatheringDetailQueries);
    }

    public ApiResponse gatheringCategory(String category, Integer pageNum, Integer pageSize) {

            PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.Direction.ASC,"id");
            Page<GatheringsQuery> page = gatheringRepository.gatheringsCategory(pageRequest,category);
            boolean hasNext = page.hasNext();
            List<GatheringsResponse> content = toContent(page);
            return GatheringCategoryResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,content,hasNext);
    }

    public ApiResponse gatheringsLike(int pageNum, int pageSize, Long userId) {

            userRepository.findById(userId)
                    .orElseThrow(()->new NotFoundUserException("no exist User!!"));
            PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.Direction.DESC,"id");
            Page<GatheringsQuery> page = gatheringRepository.gatheringsLike(pageRequest, userId);
            boolean hasNext = page.hasNext();
            List<GatheringsResponse> content = toContent(page);
            return GatheringLikeResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,content,hasNext);
    }

    public ApiResponse gatherings() {

//            List<MainGatheringsQuery> mainGatheringsQueryList = gatheringRepository.gatherings();
        List<MainGatheringsQuery> mainGatheringsQueryList = CategoryUtil.list.parallelStream()
                .map(gatheringRepository::subGatherings)
                .flatMap(List::stream)
                .toList();
        List<GatheringsQuery> gatherings = toGatheringQueriesList(mainGatheringsQueryList);
            List<MainGatheringElement> mainGatheringElements = toGatheringsResponseList(gatherings);
            Map<String, CategoryTotalGatherings> map = categorizeByCategory(mainGatheringElements);
            return toMainGatheringResponse(map);
    }

    public ApiResponse participated(Long gatheringId,Integer pageNum,Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum-1, pageSize);
        Page<ParticipatedQuery> page = gatheringRepository.gatheringParticipated(gatheringId, pageRequest);
        boolean hasNext = page.hasNext();
        List<ParticipatedQuery> participatedQueries = page.getContent();
        List<ParticipatedBy> list = participatedQueries.stream()
                .map(query -> ParticipatedBy.builder()
                        .participatedBy(query.getParticipatedBy())
                        .participatedByNickname(query.getParticipatedByNickname())
                        .participatedByUrl(url + query.getParticipatedByUrl())
                        .build())
                .toList();
        return ParticipatedByResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,list,hasNext);
    }

    private GatheringResponse getGatheringResponse(List<GatheringDetailQuery> gatheringDetailQueries){
            boolean hasNext = gatheringDetailQueries.size() >8;
            GatheringResponse gatheringResponse = GatheringFactory.toGatheringResponse(gatheringDetailQueries,hasNext
                    ,(fileUrl)->url+fileUrl);
            gatheringDetailQueries.stream()
                    .limit(8)
                    .forEach(query -> {
                        ParticipatedBy.ParticipatedByBuilder builder = ParticipatedBy.builder();
                        if (StringUtils.hasText(query.getParticipatedBy())) {
                            String participateBy = query.getParticipatedBy();
                            builder.participatedBy(participateBy);
                        }
                        if (StringUtils.hasText(query.getParticipatedByNickname())) {
                            String participateByNickname = query.getParticipatedByNickname();
                            builder.participatedByNickname(participateByNickname);
                        }
                        if (StringUtils.hasText(query.getParticipatedByUrl())) {
                            String participateByUrl = url + query.getParticipatedByUrl();
                            builder.participatedByUrl(participateByUrl);
                        }
                        gatheringResponse.getParticipatedByList().add(builder.build());
                    });
            return gatheringResponse;
    }

    private List<GatheringsResponse> toContent(Page<GatheringsQuery> page) {

            return page.map(g->GatheringsResponse.from(g,(fileUrl)->url+fileUrl))
                    .getContent();
    }

    private List<GatheringsQuery> toGatheringQueriesList(List<MainGatheringsQuery> mainGatheringsQueries) {
            return mainGatheringsQueries.stream()
                    .map(GatheringsQuery::of)
                    .collect(Collectors.toList());
    }
    private List<MainGatheringElement> toGatheringsResponseList(List<GatheringsQuery> gatheringsQueryList) {
            return gatheringsQueryList.stream()
                    .map(this::toGatheringsResponse)
                    .collect(Collectors.toList());
    }
    private Map<String, CategoryTotalGatherings> categorizeByCategory(List<MainGatheringElement> mainGatheringElements) {
            return mainGatheringElements.stream()
                    .collect(Collectors.groupingBy(
                            MainGatheringElement::getCategory,
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    this::processCategoryElements
                            )
                    ));
    }

    private MainGatheringElement toGatheringsResponse(GatheringsQuery gatheringsQuery) {

            return MainGatheringElement.from(gatheringsQuery, (fileUrl)->url+fileUrl);
    }

    private CategoryTotalGatherings processCategoryElements(List<MainGatheringElement> elements) {
            boolean hasNext = elements.size() >= 9;
            if (hasNext) {
                elements = elements.subList(0, 8);
            }

            return CategoryTotalGatherings.builder()
                    .totalGatherings(elements)
                    .hasNext(hasNext)
                    .build();
    }

    private MainGatheringResponse toMainGatheringResponse(Map<String, CategoryTotalGatherings> categoryMap) {
            return MainGatheringResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,categoryMap);
    }

    private Image saveImage(Image image,MultipartFile file) throws IOException {
            if(file != null && !file.isEmpty()){
                String url = s3ImageUploadService.upload(file);
                String contentType = file.getContentType();
                if(StringUtils.hasText(url)){
                    image = Image.builder()
                            .url(url)
                            .contentType(contentType)
                            .build();
                }
            }
            return image;
    }
}
