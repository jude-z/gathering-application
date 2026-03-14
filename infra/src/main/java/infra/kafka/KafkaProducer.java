package infra.kafka;

import infra.kafka.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Component
public class KafkaProducer {

    private final OutboxRepository outboxRepository;

    private final KafkaTemplate<String,String> kafkaTemplate;


    @Async("publishEventExecutor")
    public void publishEvent(Event<EventPayload> event) {
        try {
            EventPayload payload = event.getPayload();
            EventType eventType = event.getType();
            String topic = eventType.getTopic();
            String partitionKey = String.valueOf(payload.getPartitionKey());

            kafkaTemplate.send(
                    topic,
                    partitionKey,
                    DataSerializer.serialize(payload)
            );
        } catch (Exception e) {
            log.error("error",e);
        }
    }

    @Scheduled(
            fixedDelay = 10,
            initialDelay = 5,
            timeUnit = TimeUnit.SECONDS,
            scheduler = "publishPendingEventExecutor"
    )
    public void publishPendingEvent() {
        try {
            List<Outbox> outboxes = outboxRepository.findAllByProcessedFalseAndCreatedAtBefore(
                    PageRequest.of(0, 100, Sort.by("createdAt").descending()),
                    LocalDateTime.now());

            outboxes.stream().forEach(outbox -> {
                EventType eventType = outbox.getEventType();
                Class<? extends EventPayload> payloadClass = eventType.getPayloadClass();
                String payloadString = outbox.getPayload();
                EventPayload payload = DataSerializer.deserialize(payloadString, payloadClass);


                kafkaTemplate.send(
                        eventType.getTopic(),
                        String.valueOf(payload.getPartitionKey()),
                        DataSerializer.serialize(payload)
                );

            });
        } catch (Exception e) {
            log.error("error",e);
        }
    }
}
