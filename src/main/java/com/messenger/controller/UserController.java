package com.messenger.controller;

import com.messenger.dto.UserDto;
import com.messenger.payload.LoginResponse;
import com.messenger.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/account")
    public ResponseEntity<?> getUserDetails() {
        return ResponseEntity
                .ok()
                .body(userService.getAuthenticatedAccount());
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity
                .ok()
                .body(userService.getAllUsers());
    }

    @GetMapping("/add-friend/{id}")
    public ResponseEntity<?> addNewFriend(@PathVariable("id") Long friendId) {
        return ResponseEntity
                .ok()
                .body(userService.getAllUsers());
    }



}
