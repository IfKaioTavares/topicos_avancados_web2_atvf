package com.ifba.iotManagement.shared;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface BaseRepository<T extends AbstractEntity> extends JpaRepository<T, Long> {

    Optional<T> findByIdAndDeletedFalse(Long id);
    Optional<T> findByPublicIdAndDeletedFalse(UUID publicId);

    default void softDelete(T entity) {
       if(!entity.getDeleted()){
           entity.markAsDeleted();
       }
       this.save(entity);
    }

    default void softDeleteById(Long id) {
        Optional<T> entity = this.findByIdAndDeletedFalse(id);
        entity.ifPresent(this::softDelete);
    }
}
