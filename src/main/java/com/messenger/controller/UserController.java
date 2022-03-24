package com.messenger.controller;

import com.messenger.dto.UserDto;
import com.messenger.payload.LoginResponse;
import com.messenger.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<?> getUserDetails() {
        return ResponseEntity
                .ok()
                .body(userService.getAuthenticatedAccount());
    }

}
