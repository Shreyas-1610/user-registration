package com.shreyas.email_verification.user;

import java.util.List;
import java.util.Optional;

import com.shreyas.email_verification.registration.RegistrationRequest;
import com.shreyas.email_verification.registration.token.VerificationToken;

public interface IUserService {

    List<User> getUsers();

    User registerUser(RegistrationRequest request);

    Optional<User> findByEmail(String email);

    void saveUserVerificationToken(User theUser, String verificationToken);
    
    String validateToken(String theToken);

    VerificationToken generateNewVerificationToken(String oldToken);
}
