package com.messenger.service;

import com.messenger.domain.MessengerUserDetails;
import com.messenger.exception.BusinessException;
import com.messenger.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MessengerUserService implements UserDetailsService {

    private final UserRepository userRepository;

    public MessengerUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        try {
            return userRepository
                    .findByEmail(email)
                    .map(MessengerUserDetails:: new)
                    .orElseThrow(() -> new UsernameNotFoundException("Could not find user with email: " + email));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
