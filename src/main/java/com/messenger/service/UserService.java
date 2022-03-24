package com.messenger.service;

import com.messenger.dto.UserDto;
import com.messenger.exception.BusinessException;
import com.messenger.domain.User;
import com.messenger.payload.ApiResponse;
import com.messenger.repository.UserRepository;
import com.messenger.util.FileUploadUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    public ApiResponse register(UserDto userDto) {

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new BusinessException("User already exist", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setAbout(userDto.getAbout());

        try {
            User savedUser = userRepository.save(user);
            String fileName = StringUtils.cleanPath(userDto.getAvatar_file().getOriginalFilename());

            user.setAvatar("/user-photos/" + savedUser.getId() + "/" + fileName);
            String uploadDir = "user-photos/" + savedUser.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, userDto.getAvatar_file());
            userRepository.save(savedUser);

            return new ApiResponse(true, "User add successfully");
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String login(String email, String password) {

        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = tokenService.generateToken(authenticate);

        return token;
    }

    public User getAuthenticatedAccount() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(userEmail).get();
        return user;
    }

}
