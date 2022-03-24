package com.messenger.dto;

import com.messenger.domain.User;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String avatar;
    private MultipartFile avatar_file;
    private String about;
    private List<User> friends;
    private List<User> requests;
}
