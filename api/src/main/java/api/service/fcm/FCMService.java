package api.service.fcm;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring.myproject.dto.request.fcm.TokenNotificationRequestDto;
import spring.myproject.dto.request.fcm.TopicNotificationRequestDto;
import spring.myproject.entity.fcm.FCMToken;
import spring.myproject.repository.fcm.FCMTokenRepository;
import spring.myproject.repository.fcm.FCMTokenTopicRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


@Service
@RequiredArgsConstructor
@Slf4j
public class FCMService {

	private final FCMTokenRepository fcmTokenRepository;
	private final FCMTokenTopicRepository fcmTokenTopicRepository;


	public void sendByTopic(TopicNotificationRequestDto topicNotificationRequestDto) {
		Message message = Message.builder()
			.setTopic(topicNotificationRequestDto.getTopic())
			.setNotification(Notification.builder()
				.setTitle(topicNotificationRequestDto.getTitle())
				.setBody(topicNotificationRequestDto.getContent())
				.setImage(topicNotificationRequestDto.getImg())
				.build())
			.build();

		try {
			FirebaseMessaging.getInstance().send(message);
		} catch (FirebaseMessagingException e) {
			throw new RuntimeException(e);
		}
	}
	public void sendByToken(TokenNotificationRequestDto tokenNotificationRequestDto, List<FCMToken> tokens) {

		List<FCMToken> failedTokens = new ArrayList<>();

		for (FCMToken token : tokens) {
			Message message = Message.builder()
					.setToken(token.getTokenValue())
					.setNotification(Notification.builder()
							.setTitle(tokenNotificationRequestDto.getTitle())
							.setBody(tokenNotificationRequestDto.getContent())
							.setImage(tokenNotificationRequestDto.getImg())
							.build())
					.build();
			try {
				FirebaseMessaging.getInstance().send(message);
			} catch (FirebaseMessagingException e) {
				failedTokens.add(token);
			}
		}

		if (!failedTokens.isEmpty()) {
			List<String> failedTokenList = failedTokens.stream()
					.map(FCMToken::getTokenValue)
					.toList();
			fcmTokenRepository.deleteByTokenValueIn(failedTokenList);
		}

	}

	public void subscribeToTopic(String topicName, List<String> tokens) {

		List<String> failedTokens = new ArrayList<>();
		try {
			TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopicAsync(tokens, topicName).get();
			if (response.getFailureCount() > 0) {
				response.getErrors().forEach(error -> {
					failedTokens.add(tokens.get(error.getIndex()));
				});
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException();
		}
		if (!failedTokens.isEmpty()) {
			fcmTokenTopicRepository.deleteByTokenValueIn(failedTokens);
			fcmTokenRepository.deleteByTokenValueIn(failedTokens);
		}
	}
	public void unsubscribeFromTopic(String topic, List<String> tokens) {

		List<String> failedTokens = new ArrayList<>();
		try {
			TopicManagementResponse response = FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(tokens, topic).get();
			if (response.getFailureCount() > 0) {
				response.getErrors().forEach(error -> {
					failedTokens.add(tokens.get(error.getIndex()));
				});
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException();
		}
		if (!failedTokens.isEmpty()) {
			fcmTokenRepository.deleteByTokenValueIn(failedTokens);
		}
	}
}
