package com.messenger.controller;

import com.messenger.dto.UserDto;
import com.messenger.payload.LoginResponse;
import com.messenger.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/register",  method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity<?> addChipType(@ModelAttribute UserDto userDto) {

        return ResponseEntity
                .ok()
                .body(userService.register(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserDto userDto) {

        String token = userService.login(userDto.getEmail(), userDto.getPassword());
        return ResponseEntity
                .ok()
                .body(new LoginResponse(userDto.getEmail(), token));
    }

}
