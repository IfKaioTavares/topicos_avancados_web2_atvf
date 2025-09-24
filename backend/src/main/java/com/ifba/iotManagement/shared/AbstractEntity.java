package com.ifba.iotManagement.shared;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@Getter
public abstract class AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID publicId = UUID.randomUUID();
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private Boolean deleted = false;

    protected AbstractEntity() {}

    public void touch() {
        this.updatedAt = Instant.now();
    }

    @PostLoad
    private void validateAfterLoad(){
        validate();
    }

    protected abstract void validate();

    public void markAsDeleted() {
        this.deleted = true;
        touch();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AbstractEntity that = (AbstractEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}