package com.messenger.service;

import com.auth0.jwt.JWT;
import com.messenger.domain.MessengerUserDetails;
import com.messenger.util.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Service
public class TokenService {

    public String generateToken(Authentication authResult) {
        MessengerUserDetails userDetails = (MessengerUserDetails) authResult.getPrincipal();

        // Create JWT Token
        String token = JWT.create()
                .withSubject(userDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .sign(HMAC512(JwtProperties.SECRET.getBytes()));

        return token;
    }

}
