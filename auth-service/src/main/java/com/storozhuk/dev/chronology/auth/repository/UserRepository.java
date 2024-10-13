package com.storozhuk.dev.chronology.auth.repository;

import com.storozhuk.dev.chronology.auth.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
    extends JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {
  Optional<UserEntity> findByEmail(String email);
}
