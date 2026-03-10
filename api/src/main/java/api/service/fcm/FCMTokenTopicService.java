package api.service.fcm;

import api.common.mapper.FCMTokenTopicMapper;
import infra.dto.querydsl.QueryDslPageResponse;
import entity.fcm.FCMToken;
import entity.fcm.FCMTokenTopic;
import entity.fcm.Topic;
import entity.fcm.UserTopic;
import entity.user.User;
import exception.CommonException;
import infra.repository.fcm.JdbcFcmRepository;
import infra.repository.fcm.FCMTokenRepository;
import infra.repository.fcm.FCMTokenTopicRepository;
import infra.repository.fcm.TopicRepository;
import infra.repository.fcm.UserTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import infra.repository.fcm.QueryDslFcmRepository;
import infra.repository.user.QueryDslUserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static api.requeset.fcm.FcmRequestDto.*;
import static api.requeset.user.UserRequestDto.*;
import static exception.Status.*;


@Service
@RequiredArgsConstructor
@Transactional
public class FCMTokenTopicService {

    private final TopicRepository topicRepository;
    private final FCMTokenRepository fcmTokenRepository;
    private final FCMTokenTopicRepository fcmTokenTopicRepository;
    private final UserTopicRepository userTopicRepository;
    private final QueryDslUserRepository queryDslUserRepository;
    private final QueryDslFcmRepository queryDslFcmRepository;
    private final JdbcFcmRepository jdbcFcmRepository;

    final int TOKEN_EXPIRATION_MONTHS = 2;

    public void saveFCMToken(SignInRequest signInRequest) {

        User user = queryDslUserRepository.findByUsername(signInRequest.getUsername())
                .orElseThrow(() -> new CommonException(NOT_FOUND_USER));
        Long userId = user.getId();
        Optional<FCMToken> existingToken = queryDslFcmRepository.findByTokenValueAndUser(signInRequest.getFcmToken(), user.getId());
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
            QueryDslPageResponse<UserTopic> queryDslPageResponse = queryDslFcmRepository.findByUserId(userId);
            List<UserTopic> content = queryDslPageResponse.getContent();
            List<Topic> subscribedTopics = content.stream()
                    .map(UserTopic::getTopic)
                    .distinct()
                    .collect(Collectors.toList()).reversed();

            List<FCMTokenTopic> newSubscriptions = subscribedTopics.stream()
                    .map(topic -> FCMTokenTopicMapper.toFCMTokenTopic(topic, fcmToken))
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
                .orElseThrow(() -> new CommonException(NOT_FOUND_TOPIC));


        User user = queryDslUserRepository.findAndTokenByUserId(userId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_USER));

        if (queryDslFcmRepository.existsByTopicAndUser(topicName, userId)) {
            throw new CommonException(ALREADY_SUBSCRIBE_TOPIC);
        }

        List<FCMToken> userTokens = user.getTokens();

        UserTopic userTopic = UserTopic.builder()
                .topic(topic)
                .user(user)
                .build();
        userTopicRepository.save(userTopic);

        List<FCMTokenTopic> topicTokens = userTokens.stream()
                .map(token -> FCMTokenTopicMapper.toFCMTokenTopic(topic, token))
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
                .orElseThrow(() -> new CommonException(NOT_FOUND_TOPIC));

        User user = queryDslUserRepository.findAndTokenByUserId(userId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_USER));

        List<FCMToken> memberTokens = user.getTokens();
        List<String> tokenValues = memberTokens.stream()
                .map(FCMToken::getTokenValue)
                .collect(Collectors.toList());
        jdbcFcmRepository.deleteUserTopicByTopicAndUser(topic,user);
        jdbcFcmRepository.deleteTokenTopicByTokenValueIn(tokenValues);

//        fcmService.unsubscribeFromTopic(topicName, tokenValues);
    }
    public void sendByToken(TokenNotificationRequestDto tokenNotificationRequestDto, List<FCMToken> tokens) {
//        fcmService.sendByToken(tokenNotificationRequestDto, tokens);
    }



}
