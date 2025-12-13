package com.pharmacy.assistant.domain.model.common;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public abstract class BaseEntity {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void prePersist() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }
}