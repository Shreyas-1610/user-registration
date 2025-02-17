package com.shreyas.email_verification.event.listener;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.shreyas.email_verification.event.RegistrationCompleteEvent;
import com.shreyas.email_verification.user.User;
import com.shreyas.email_verification.user.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    private final UserService userService;
    private final JavaMailSender mailSender;
    private User theUser;
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        
        //1) Get new user
        theUser = event.getUser();
        //2) Create verification token
        String verificationToken = UUID.randomUUID().toString();
        //3) Save the token
        userService.saveUserVerificationToken(theUser, verificationToken);
        //4) Build the verification url to be sent to the user
        String url = event.getApplicationUrl()+"/register/verifyEmail?token="+verificationToken;
        try {
            //5) send the email
            sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        } 
        log.info("Click the link to complete the registration process:  {}",url);

    }
    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException{
        String subject = "Email Verification";
        String senderName = "User Registration Portal";
        String content = "<p> Hi, "+theUser.getFirstName()+",</p>"+
            "<p> Thank you for registering with us,"+""+
            "Please follow the link below to complete the registration.</p>"+
            "<a href =\""+url+"\">Verify your email to activate your account</a>"+
            "<p> Thank you <br> User Registration Portal Service";
            MimeMessage message = mailSender.createMimeMessage();
            var messageHelper = new MimeMessageHelper(message);
            messageHelper.setFrom("kshreyask1@gmail.com",senderName);
            messageHelper.setTo(theUser.getEmail());
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);
            mailSender.send(message);
            
    }
    
}
