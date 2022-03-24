package com.messenger.service;

import com.messenger.dto.UserDto;
import com.messenger.exception.BusinessException;
import com.messenger.model.User;
import com.messenger.payload.ApiResponse;
import com.messenger.repository.UserRepository;
import com.messenger.util.FileUploadUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ApiResponse register(UserDto userDto) {

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new BusinessException("User already exist", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
}
