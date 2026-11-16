package pos.infrastructure.id;

import org.springframework.stereotype.Component;
import pos.domain.port.IdGenerator;

@Component
public class SequentialIdGenerator implements IdGenerator {
    private long nextId = 1;

    @Override
    public Long nextId() {
        return nextId++;
    }
}
