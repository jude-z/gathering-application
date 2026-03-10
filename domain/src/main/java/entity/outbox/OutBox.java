package entity.outbox;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "outbox")
public class OutBox {
    private Long id;
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    private boolean processed;
}
