package com.shreyas.email_verification.registration.token;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    public VerificationToken findByToken(String token);

}
