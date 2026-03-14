package entity.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "outbox")
public class OutBox {
    @Id
    @Column(name = "outbox_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    private boolean processed;
}
