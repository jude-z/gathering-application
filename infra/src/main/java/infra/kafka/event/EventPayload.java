package infra.kafka.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public abstract class EventPayload {
    private Long partitionKey;
    private Long eventId;

}
