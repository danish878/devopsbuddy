package com.devopsbuddy.backend.persistence.repositories;

import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);

    @Query("SELECT prt FROM PasswordResetToken prt INNER JOIN prt.user u WHERE prt.user.id = ?1")
    Set<PasswordResetToken> findAllByUserId(long userId);
}
