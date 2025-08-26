package com.ai.dailyReport.auth.service;

import com.ai.dailyReport.domain.entity.User;
import com.ai.dailyReport.repository.UserRepository;
import lombok.RequiredArgsConstructor;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // JWT 시크릿 키를 String으로 받아서 Key로 변환
    private Key getSecretKey() {
      return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
    
    public User validateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        
        throw new RuntimeException("Invalid credentials");
    }
    
    public String generateJwt(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(user.getUserId().toString())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getSecretKey())
            .compact();
    }
}