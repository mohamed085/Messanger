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

    @GetMapping("/discover-new-friends")
    public ResponseEntity<?> discoverNewFriends() {
        return ResponseEntity
                .ok()
                .body(userService.discoverNewFriends());
    }

    @GetMapping("/add-friend/{id}")
    public ResponseEntity<?> addNewFriend(@PathVariable("id") Long friendId) {
        return ResponseEntity
                .ok()
                .body(userService.addNewFriend(friendId));
    }

    @GetMapping("/friend-requests")
    public ResponseEntity<?> getAllFriendRequests() {
        return ResponseEntity
                .ok()
                .body(userService.getAllFriendRequests());
    }

    @GetMapping("/accept-requests/{id}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable("id") Long friendId) {
        return ResponseEntity
                .ok()
                .body(userService.acceptFriendRequest(friendId));
    }

    @GetMapping("/reject-requests/{id}")
    public ResponseEntity<?> rejectFriendRequest(@PathVariable("id") Long friendId) {
        return ResponseEntity
                .ok()
                .body(userService.rejectFriendRequest(friendId));
    }

}
