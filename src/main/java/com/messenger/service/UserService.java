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

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        user.getFriends().forEach(user1 -> {
            user1.setRequests(null);
            user1.setFriends(null);
        });

        user.getRequests().forEach(user1 -> {
            user1.setRequests(null);
            user1.setFriends(null);
        });

        return user;
    }

    public List<User> discoverNewFriends() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User loginUser = userRepository.findByEmail(userEmail).get();

        List<User> users = new ArrayList<>();

        userRepository.discoverNewUsers(loginUser.getId()).forEach(user -> {
            user.setFriends(null);
            user.setRequests(null);

            users.add(user);
        });

        return users;
    }

    @Transactional
    public ApiResponse addNewFriend(Long friendId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(userEmail).get();

        User newFriend = userRepository.findById(friendId)
                .orElseThrow(() -> new BusinessException("User friend not found", HttpStatus.NOT_FOUND));

        newFriend.getRequests().forEach(user1 -> {
            if (user1.getId().equals(user.getId())) {
                throw new BusinessException("User already added", HttpStatus.BAD_REQUEST);
            }
        });

        newFriend.getRequests().add(user);

        userRepository.save(newFriend);

        return new ApiResponse(true, "Request send successfully");
    }

    public Set<User> getAllFriendRequests() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(userEmail).get();
        user.getRequests().forEach(user1 -> {
            user1.setFriends(null);
            user1.setRequests(null);
        });
        return user.getRequests();
    }

    public ApiResponse acceptFriendRequest(Long id) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(userEmail).get();

        User newFriend = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User friend not found", HttpStatus.NOT_FOUND));

        if (user.getRequests().contains(newFriend)) {
            Set<User> newRequests = new HashSet<>();
            user.getRequests().forEach(user1 -> {
                if (!user1.getId().equals(newFriend.getId())) {
                    newRequests.add(user1);
                }
            });

            user.setRequests(null);
            user.setRequests(newRequests);
            user.getFriends().add(newFriend);

            newFriend.getFriends().add(user);
            userRepository.save(user);
            userRepository.save(newFriend);

            return new ApiResponse(true, "User accepted successfully");
        }

        throw new BusinessException("User not request you", HttpStatus.BAD_REQUEST);
    }

    public ApiResponse rejectFriendRequest(Long id) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(userEmail).get();

        User newFriend = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User friend not found", HttpStatus.NOT_FOUND));

        Set<User> newRequests = new HashSet<>();
        user.getRequests().forEach(user1 -> {
            if (!user1.getId().equals(id)) {
                newRequests.add(user1);
            }
        });

        user.setRequests(null);
        user.setRequests(newRequests);
        userRepository.save(user);

        return new ApiResponse(true, "User rejected successfully");

    }
}
