package com.neurotutor.user_service.repository;

import com.neurotutor.user_service.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByTokenAndEmail(String token, String email);
    Optional<PasswordResetToken> findByEmail(String email);
}
