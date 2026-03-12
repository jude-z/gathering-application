package api.service.gathering;

import api.common.mapper.CategoryMapper;
import api.common.mapper.EnrollmentMapper;
import api.common.mapper.GatheringMapper;
import api.response.ApiDataResponse;
import api.response.ApiResponse;
import api.service.image.ImageUploadService;
import util.page.PageableInfo;
import infra.repository.dto.jdbc.gathering.GatheringDetailProjection;
import infra.repository.dto.querydsl.QueryDslPageResponse;
import infra.repository.dto.querydsl.gathering.GatheringsProjection;
import infra.repository.dto.querydsl.gathering.ParticipatedProjection;
import entity.category.Category;
import entity.enrollment.Enrollment;
import entity.gathering.Gathering;
import entity.image.Image;
import entity.user.User;
import exception.CommonException;
import exception.Status;
import infra.repository.gathering.JdbcGatheringRepository;
import infra.repository.category.CategoryRepository;
import infra.repository.enrollment.EnrollmentRepository;
import infra.repository.gathering.GatheringRepository;
import infra.repository.image.ImageRepository;
import infra.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import infra.repository.category.QueryDslCategoryRepository;
import infra.repository.gathering.QueryDslGatheringRepository;
import common.CategoryUtil;
import util.page.PageCalculator;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static api.requeset.gathering.GatheringRequestDto.*;
import static api.response.gathering.GatheringResponseDto.*;

@Service
@Transactional
@RequiredArgsConstructor
public class GatheringService {

    private final GatheringRepository gatheringRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ImageRepository imageRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final QueryDslCategoryRepository queryDslCategoryRepository;
    private final QueryDslGatheringRepository queryDslGatheringRepository;
    private final JdbcGatheringRepository jdbcGatheringRepository;
    private final ImageUploadService imageUploadService;
    @Value("${file.path}")
    private String path;

    public ApiResponse addGathering(AddGatheringRequest addGatheringRequest, MultipartFile file, Long userId) throws IOException {

            User user = userRepository.findById(userId)
                    .orElseThrow(()->new CommonException(Status.NOT_FOUND_USER));
            if(!CategoryUtil.existCategory(addGatheringRequest.getCategory())){
                throw new CommonException(Status.NOT_FOUND_CATEGORY);
            }
            Image image = null;
            image = saveImage(image,file);
            Category category = CategoryMapper.toCategory(addGatheringRequest.getCategory());
            Gathering gathering = GatheringMapper.toGathering(addGatheringRequest,user,image,category);
            Enrollment enrollment = EnrollmentMapper.toEnrollment(true, gathering, user);
            if(image!=null) imageRepository.save(image);
            gatheringRepository.save(gathering);
            categoryRepository.save(category);
            enrollmentRepository.save(enrollment);
            return ApiDataResponse.of(gathering.getId(),Status.SUCCESS);
    }

    public ApiResponse updateGathering(UpdateGatheringRequest updateGatheringRequest, MultipartFile file, Long userId, Long gatheringId) throws IOException {

            userRepository.findById(userId)
                    .orElseThrow(()->new CommonException(Status.NOT_FOUND_USER));
            Category category = queryDslCategoryRepository.findBy(gatheringId, updateGatheringRequest.getCategory())
                    .orElseThrow(()-> new CommonException(Status.NOT_FOUND_CATEGORY));
            Gathering gathering = queryDslGatheringRepository.findGatheringFetchCreatedBy(gatheringId)
                    .orElseThrow(()->new CommonException(Status.NOT_FOUND_GATHERING));
            User createBy = gathering.getCreateBy();
            boolean authorize = ObjectUtils.nullSafeEquals(createBy.getId(),userId);
            if(!authorize) throw new CommonException(Status.NOT_AUTHORIZE);
            Image image = null;
            image = saveImage(image,file);
            if(image!=null) imageRepository.save(image);
            GatheringMapper.updateGathering(gathering, updateGatheringRequest, image);
            CategoryMapper.updateCategory(category,updateGatheringRequest.getCategory());
            return ApiDataResponse.of(gathering.getId(),Status.SUCCESS);
    }

    public ApiResponse gatheringDetail(Long gatheringId){

            List<GatheringDetailProjection> gatheringDetailProjectionList = jdbcGatheringRepository.gatheringDetail(gatheringId);
            if(gatheringDetailProjectionList.isEmpty()) throw new CommonException(Status.NOT_FOUND_GATHERING);
            GatheringResponse gatheringResponse =  getGatheringResponse(gatheringDetailProjectionList);
            return ApiDataResponse.of(gatheringResponse,Status.SUCCESS);
    }

    public ApiResponse gatheringCategory(String category, Integer pageNum, Integer pageSize) {
            PageableInfo pageableInfo = PageCalculator.toPageableInfo(pageNum, pageSize);
            QueryDslPageResponse<GatheringsProjection> queryDslPageResponse = queryDslGatheringRepository.gatheringsCategory(pageableInfo,category);
            return ApiDataResponse.of(queryDslPageResponse,Status.SUCCESS);
    }

    public ApiResponse gatheringsLike(int pageNum, int pageSize, Long userId) {

            userRepository.findById(userId)
                    .orElseThrow(()->new CommonException(Status.NOT_FOUND_USER));
            PageableInfo pageableInfo = PageCalculator.toPageableInfo(pageNum, pageSize);
            QueryDslPageResponse<GatheringsProjection> queryDslPageResponse = queryDslGatheringRepository.gatheringsLike(pageableInfo, userId);
            return ApiDataResponse.of(queryDslPageResponse,Status.SUCCESS);
    }

    public ApiResponse gatherings() {
        List<GatheringsProjection> gatheringsProjections = CategoryUtil.list.stream()
                .map(jdbcGatheringRepository::subGatherings)
                .flatMap(List::stream)
                .map(GatheringsProjection::of)
                .toList();
        List<MainGatheringElement> mainGatheringElements = gatheringsProjections.stream()
                .map(projection -> MainGatheringElement.from(projection, url -> path + url))
                .toList();
        Map<String, CategoryTotalGatherings> map = categorizeByCategory(mainGatheringElements);
        return toMainGatheringResponse(map);
    }

    public ApiResponse participated(Long gatheringId,Integer pageNum,Integer pageSize) {
        PageableInfo pageableInfo = PageCalculator.toPageableInfo(pageNum, pageSize);
        QueryDslPageResponse<ParticipatedProjection> queryDslPageResponse = queryDslGatheringRepository.gatheringParticipated(pageableInfo, gatheringId);
        List<ParticipatedProjection> content = queryDslPageResponse.getContent();
        List<ParticipatedBy> list = content.stream()
                .map(element -> ParticipatedBy.builder()
                        .participatedBy(element.getParticipatedBy())
                        .participatedByNickname(element.getParticipatedByNickname())
                        .participatedByUrl(path + element.getParticipatedByUrl())
                        .build())
                .toList();
        return ApiDataResponse.of(content,Status.SUCCESS);
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

    private GatheringResponse getGatheringResponse(List<GatheringDetailProjection> gatheringDetailProjectionList){
            boolean hasNext = gatheringDetailProjectionList.size() >8;
            GatheringResponse gatheringResponse = GatheringMapper.toGatheringResponse(gatheringDetailProjectionList,hasNext
                    ,url -> path + url);
            gatheringDetailProjectionList.stream()
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
                            String participateByUrl = path + query.getParticipatedByUrl();
                            builder.participatedByUrl(participateByUrl);
                        }
                        gatheringResponse.getParticipatedByList().add(builder.build());
                    });
            return gatheringResponse;
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

    private ApiResponse toMainGatheringResponse(Map<String, CategoryTotalGatherings> categoryMap) {
        return ApiDataResponse.of(categoryMap, Status.SUCCESS);
    }

    private Image saveImage(Image image,MultipartFile file) throws IOException {
            if(file != null && !file.isEmpty()){
                String url = imageUploadService.upload(file);
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
