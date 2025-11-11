package com.crud.backend.common;

import java.time.Instant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter @Setter
public abstract class Auditable {
    @Column(name = "created_at")
    protected Instant createdAt;
    @Column(name = "updated_at")
    protected Instant updatedAt;
    @PrePersist
    protected void prePersist(){
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        updatedAt = createdAt;
    }
    @PreUpdate
    protected void preUpdate(){
        updatedAt = Instant.now();
    }
}
