
package com.shreyas.email_verification.registration;

import java.io.UnsupportedEncodingException;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shreyas.email_verification.event.RegistrationCompleteEvent;
import com.shreyas.email_verification.event.listener.RegistrationCompleteEventListener;
import com.shreyas.email_verification.registration.token.VerificationToken;
import com.shreyas.email_verification.registration.token.VerificationTokenRepository;
import com.shreyas.email_verification.user.User;
import com.shreyas.email_verification.user.UserService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository tokenRepository;
    private final RegistrationCompleteEventListener eventListener;
    private final HttpServletRequest servletRequest;

    @PostMapping
    public String registerUser(@RequestBody RegistrationRequest registrationRequest, final HttpServletRequest request){
        User user = userService.registerUser(registrationRequest);
        //publish registration event
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return "Success! Please check your Email to verify and complete the registration process.";
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token){
        String url = applicationUrl(servletRequest)+"/register/resend-verification-token?token="+token;
        VerificationToken theToken = tokenRepository.findByToken(token);
        if(theToken.getUser().isEnabled()){
            return "The account is already verified";
        }
        String verificationResult = userService.validateToken(token);
        if(verificationResult.equalsIgnoreCase("Valid successfully")){
            return "Email verified successfully. You may login into your account";
        }
        return "Invalid verification link, <a href=\""+url+"\"> Get a new verification link. </a>";
    }

    @GetMapping("/resend-verification-token")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException{
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        User theUser = verificationToken.getUser();
        resendVerificationTokenEmail(theUser, applicationUrl(request), verificationToken);
        return "New verification link has been sent.";
    }
    

    private void resendVerificationTokenEmail(User theUser, String applicationUrl, VerificationToken verificationToken) throws MessagingException, UnsupportedEncodingException {
        String url = applicationUrl+"/register/verifyEmail?token="+verificationToken.getToken();
        eventListener.sendVerificationEmail(url);
        log.info("Click the link to complete the registration process:  {}",url);
    }

    public String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
    }
}
