package api.service.fcm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.myproject.common.exception.fcm.AlreadySubscribeTopicException;
import spring.myproject.common.exception.fcm.NotFoundTopicException;
import spring.myproject.common.exception.user.NotFoundUserException;
import spring.myproject.dto.request.fcm.TokenNotificationRequestDto;
import spring.myproject.entity.fcm.FCMToken;
import spring.myproject.entity.fcm.FCMTokenTopic;
import spring.myproject.entity.fcm.Topic;
import spring.myproject.entity.fcm.UserTopic;
import spring.myproject.entity.user.User;
import spring.myproject.repository.fcm.FCMTokenRepository;
import spring.myproject.repository.fcm.FCMTokenTopicRepository;
import spring.myproject.repository.fcm.TopicRepository;
import spring.myproject.repository.fcm.UserTopicRepository;
import spring.myproject.repository.user.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static spring.myproject.dto.request.user.UserRequestDto.*;

@Service
@RequiredArgsConstructor
@Transactional
public class FCMTokenTopicService {

    private final TopicRepository topicRepository;
    private final FCMTokenRepository fcmTokenRepository;
    private final FCMTokenTopicRepository fcmTokenTopicRepository;
    private final UserTopicRepository userTopicRepository;
    private final UserRepository userRepository;
    private final FCMService fcmService;

    final int TOKEN_EXPIRATION_MONTHS = 2;

    public void saveFCMToken(SignInRequest signInRequest) {

        User user = userRepository.findByUsername(signInRequest.getUsername())
                .orElseThrow(() -> new NotFoundUserException("User not found"));
        Long userId = user.getId();
        Optional<FCMToken> existingToken = fcmTokenRepository.findByTokenValueAndUser(signInRequest.getFcmToken(), user.getId());
        if (existingToken.isPresent()) {
            FCMToken fcmToken = existingToken.get();
            fcmToken.changeExpirationDate(2);
        } else {
            FCMToken fcmToken = FCMToken.builder()
                    .tokenValue(signInRequest.getFcmToken())
                    .user(user)
                    .expirationDate(LocalDate.now().plusMonths(TOKEN_EXPIRATION_MONTHS))
                    .build();
            fcmTokenRepository.save(fcmToken);

            List<UserTopic> userTopics = userTopicRepository.findByUserId(userId);
            List<Topic> subscribedTopics = userTopics.stream()
                    .map(UserTopic::getTopic)
                    .distinct()
                    .collect(Collectors.toList()).reversed();

            List<FCMTokenTopic> newSubscriptions = subscribedTopics.stream()
                    .map(topic -> new FCMTokenTopic(topic, fcmToken))
                    .collect(Collectors.toList());
            fcmTokenTopicRepository.saveAll(newSubscriptions);

//            for (Topic topic : subscribedTopics) {
//                fcmService.subscribeToTopic(topic.getTopicName(), Collections.singletonList(fcmToken.getTokenValue()));
//            }
        }
    }

    @Transactional
    public void subscribeToTopic(String topicName, Long userId) {

        Topic topic = topicRepository.findByTopicName(topicName)
                .orElseThrow(() -> new NotFoundTopicException("Not Found Topic"));


        User user = userRepository.findAndTokenByUserId(userId)
                .orElseThrow(() -> new NotFoundUserException("Not Found User"));

        if (userTopicRepository.existsByTopicAndUser(topicName, userId)) {
            throw new AlreadySubscribeTopicException("already subscribe topic");
        }

        List<FCMToken> userTokens = user.getTokens();

        UserTopic userTopic = UserTopic.builder()
                .topic(topic)
                .user(user)
                .build();
        userTopicRepository.save(userTopic);

        List<FCMTokenTopic> topicTokens = userTokens.stream()
                .map(token -> new FCMTokenTopic(topic, token))
                .collect(Collectors.toList());
        fcmTokenTopicRepository.saveAll(topicTokens);

        List<String> tokenValues = userTokens.stream()
                .map(FCMToken::getTokenValue)
                .collect(Collectors.toList());
//        fcmService.subscribeToTopic(topicName, tokenValues);
    }

    @Transactional
    public void unsubscribeFromTopic(String topicName, Long userId) {

        Topic topic = topicRepository.findByTopicName(topicName)
                .orElseThrow(() -> new NotFoundTopicException("Not Found Topic"));

        User user = userRepository.findAndTokenByUserId(userId)
                .orElseThrow(() -> new NotFoundUserException("Not Found User"));

        List<FCMToken> memberTokens = user.getTokens();
        List<String> tokenValues = memberTokens.stream()
                .map(FCMToken::getTokenValue)
                .collect(Collectors.toList());
        userTopicRepository.deleteByTopicAndUser(topic, user);
        fcmTokenTopicRepository.deleteByTokenValueIn(tokenValues);

//        fcmService.unsubscribeFromTopic(topicName, tokenValues);
    }
    public void sendByToken(TokenNotificationRequestDto tokenNotificationRequestDto, List<FCMToken> tokens) {
//        fcmService.sendByToken(tokenNotificationRequestDto, tokens);
    }



}
