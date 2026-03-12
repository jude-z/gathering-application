package infra.kafka.event;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Event<T extends EventPayload> {
    private EventType type;
    private T payload;

    @Builder
    private Event(EventType type, T payload) {
        this.type = type;
        this.payload = payload;
    }

    public static Event<EventPayload> of(EventType type, EventPayload payload) {
        return Event.builder()
                .type(type)
                .payload(payload)
                .build();
    }

    public static Event fromJson(String message) {
        EventRaw raw = DataSerializer.deserialize(message, EventRaw.class);
        if (raw == null) {
            return null;
        }
        EventPayload payload = DataSerializer.deserialize(raw.payload, raw.type.getPayloadClass());
        return Event.of(raw.type, payload);
    }

    @Getter
    private static class EventRaw {
        private EventType type;
        private Object payload;
    }
}
