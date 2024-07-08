package com.shreyas.email_verification.user;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    public final UserService userService;

    @GetMapping
    public List<User> getUsers(){
        return userService.getUsers();
    }


}   
