package com.shreyas.email_verification.registration;


public record RegistrationRequest(
        String firstName,
        String lastName,
        String email,   
        String password,
        String role) {
            
}
