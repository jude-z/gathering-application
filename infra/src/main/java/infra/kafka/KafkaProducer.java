package infra.kafka;

import infra.kafka.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final OutboxRepository outboxRepository;

    private final KafkaTemplate<String,String> kafkaTemplate;


    @Async("publishEventExecutor")
    public void publishEvent(Event<EventPayload> event) {
        try {
            EventPayload payload = event.getPayload();
            EventType eventType = event.getType();
            String topic = eventType.getTopic();
            String partitionKey = payload.getPartitionKey();

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
    public void publishPendingEvent(Event<EventPayload> event) {
        try {
            List<Outbox> outboxes = outboxRepository.findAllByProcessedFalseAndCreatedAtBefore(
                    PageRequest.of(0, 100, Sort.by("createdAt").descending()),
                    LocalDateTime.now());

            outboxes.stream().forEach(outbox -> {
                EventPayload payload = event.getPayload();
                EventType eventType = event.getType();

                kafkaTemplate.send(
                        eventType.getTopic(),
                        payload.getPartitionKey(),
                        DataSerializer.serialize(payload)
                );

            });
        } catch (Exception e) {
            log.error("error",e);
        }
    }
}
