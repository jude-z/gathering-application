package spring.myproject.service.gathering;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spring.myproject.entity.category.Category;
import spring.myproject.entity.enrollment.Enrollment;
import spring.myproject.entity.fcm.Topic;
import spring.myproject.entity.gathering.Gathering;
import spring.myproject.entity.image.Image;
import spring.myproject.entity.user.Role;
import spring.myproject.entity.user.User;
import spring.myproject.common.exception.category.NotFoundCategoryException;
import spring.myproject.common.exception.gathering.NotFoundGatheringException;
import spring.myproject.common.exception.meeting.NotAuthorizeException;
import spring.myproject.common.exception.user.NotFoundUserException;
import spring.myproject.repository.category.CategoryRepository;
import spring.myproject.repository.enrollment.EnrollmentRepository;
import spring.myproject.repository.gathering.GatheringRepository;
import spring.myproject.repository.image.ImageRepository;
import spring.myproject.repository.user.UserRepository;
import spring.myproject.common.s3.S3ImageUploadService;
import spring.myproject.service.fcm.FCMTokenTopicService;
import spring.myproject.service.recommend.RecommendService;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static spring.myproject.dto.request.gathering.GatheringRequestDto.*;
import static spring.myproject.dto.response.gathering.GatheringResponseDto.*;
import static spring.myproject.utils.ConstClass.*;
import static spring.myproject.utils.MockData.*;

@ExtendWith(MockitoExtension.class)
public class GatheringServiceTest {
    @InjectMocks
    GatheringService gatheringService;
    @Mock
    GatheringRepository gatheringRepository;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    S3ImageUploadService s3ImageUploadService;
    @Mock
    FCMTokenTopicService fcmTokenTopicService;
    @Mock
    TopicRepository topicRepository;
    @Mock
    RecommendService recommendService;
    @Mock
    ImageRepository imageRepository;
    @Mock
    EnrollmentRepository enrollmentRepository;
    @DisplayName("Throws NotFoundUserException")
    @Test
    void addGatheringThrowsNotFoundUserException(){
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        AddGatheringRequest addGatheringRequest = AddGatheringRequest.builder()
                .build();

        assertThatThrownBy(()->gatheringService.addGathering(addGatheringRequest,mockMultipartFile,2L))
                .isInstanceOf(NotFoundUserException.class);
    }

    @DisplayName("Throws NotFoundCategoryException")
    @Test
    void addGatheringThrowsNotFoundCategoryException(){
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        User mockUser = returnMockUser(1L,"true username", "true password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        AddGatheringRequest addGatheringRequest = AddGatheringRequest.builder()
                .category("false categoryName")
                .build();

        assertThatThrownBy(()->gatheringService.addGathering(addGatheringRequest,mockMultipartFile,1L))
                .isInstanceOf(NotFoundCategoryException.class);
    }

    @DisplayName("Return Normal Response")
    @Test
    void addGathering() throws IOException {
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        User mockUser = returnMockUser(1L,"true username","true password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(s3ImageUploadService.upload(any(MultipartFile.class))).thenReturn("url");
        when(mockMultipartFile.getContentType()).thenReturn("image/png");
        when(imageRepository.save(any(Image.class))).thenReturn(mock(Image.class));
        when(gatheringRepository.save(any(Gathering.class))).thenReturn(mock(Gathering.class));
        when(topicRepository.save(any(Topic.class))).thenReturn(mock(Topic.class));
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(mock(Enrollment.class));
        doNothing().when(fcmTokenTopicService).subscribeToTopic(any(String.class),anyLong());
        doNothing().when(recommendService).addScore(any(),anyInt());
        AddGatheringRequest addGatheringRequest = AddGatheringRequest.builder()
                .category("category1")
                .build();

        AddGatheringResponse addGatheringResponse = gatheringService.addGathering(addGatheringRequest, mockMultipartFile, 1L);

        assertThat(addGatheringResponse)
                .extracting("code","message")
                .containsExactly(SUCCESS_CODE, SUCCESS_MESSAGE);

    }
    @DisplayName("Throws NotFoundUserException")
    @Test
    void updateGatheringThrowsNotFoundUserException(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        UpdateGatheringRequest updateGatheringRequest = UpdateGatheringRequest.builder()
                .build();

        assertThatThrownBy(()->gatheringService
                .updateGathering(updateGatheringRequest,mockMultipartFile,1L,1L))
                .isInstanceOf(NotFoundUserException.class);
    }
    @DisplayName("Throws NotFoundCategoryException")
    @Test
    void updateGatheringThrowsNotFoundCategoryException(){
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        User mockUser = returnMockUser(1L,"true username", "true password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findBy(1L,"false categoryName"))
                .thenReturn(Optional.empty());

        UpdateGatheringRequest updateGatheringRequest = UpdateGatheringRequest.builder()
                .category("false categoryName")
                .build();
        assertThatThrownBy(()->gatheringService
                .updateGathering(updateGatheringRequest,mockMultipartFile,1L,1L))
                .isInstanceOf(NotFoundCategoryException.class);
    }
    @DisplayName("Throws NotFoundGatheringException")
    @Test
    void updateGatheringThrowsNotFoundGatheringException(){
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        User mockUser = returnMockUser(1L,"true username", "true password");
        Category mockCategory = returnMockCategory("true categoryName");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findBy(1L,"true categoryName"))
                .thenReturn(Optional.of(mockCategory));
        when(gatheringRepository.findGatheringFetchCreatedByAndTokensId(1L))
                .thenReturn(Optional.empty());
        UpdateGatheringRequest updateGatheringRequest = UpdateGatheringRequest.builder()
                .category("true categoryName")
                .build();

        assertThatThrownBy(()->gatheringService
                .updateGathering(updateGatheringRequest,mockMultipartFile,1L,1L))
                .isInstanceOf(NotFoundGatheringException.class);

    }
    @DisplayName("Throws NotAuthorizeException")
    @Test
    void updateGatheringThrowsNotAuthorizeException(){
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        User mockUser1 = returnMockUser(1L,"true username", "true password");
        User mockUser2 = returnMockUser(2L,"true username", "true password");
        Category mockCategory = returnMockCategory("true categoryName");
        Gathering mockGathering = returnMockGathering(mockUser1);
        when(userRepository.findById(2L)).thenReturn(Optional.of(mockUser2));
        when(categoryRepository.findBy(1L,"true categoryName"))
                .thenReturn(Optional.of(mockCategory));
        when(gatheringRepository.findGatheringFetchCreatedByAndTokensId(1L))
                .thenReturn(Optional.of(mockGathering));
        UpdateGatheringRequest updateGatheringRequest = UpdateGatheringRequest.builder()
                .category("true categoryName")
                .build();

        assertThatThrownBy(()->gatheringService.updateGathering(updateGatheringRequest,mockMultipartFile,2L,1L))
                .isInstanceOf(NotAuthorizeException.class);

    }
    @DisplayName("Return 200 Response")
    @Test
    void updateGathering() throws IOException {
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        User mockUser = returnMockUser(1L,"true username","true password");
        Category mockCategory = returnMockCategory("true category");
        Gathering mockGathering = returnMockGathering(mockUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findBy(1L,"true category"))
                .thenReturn(Optional.of(mockCategory));
        when(gatheringRepository.findGatheringFetchCreatedByAndTokensId(1L))
                .thenReturn(Optional.of(mockGathering));
        when(s3ImageUploadService.upload(any(MultipartFile.class))).thenReturn("url");
        when(mockMultipartFile.getContentType()).thenReturn("img/png");
        when(imageRepository.save(any(Image.class))).thenReturn(mock(Image.class));

        UpdateGatheringRequest updateGatheringRequest = UpdateGatheringRequest.builder()
                .category("true category")
                .build();

        UpdateGatheringResponse updateGatheringResponse = gatheringService
                .updateGathering(updateGatheringRequest, mockMultipartFile, 1L, 1L);
        assertThat(updateGatheringResponse)
                .extracting("code","message")
                .containsExactly(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

}
