package dio.budgeting.domain;

import java.util.UUID;

public record TranscationId(UUID uuid) {

    public TranscationId(){
        this(UUID.randomUUID());
    }
}
