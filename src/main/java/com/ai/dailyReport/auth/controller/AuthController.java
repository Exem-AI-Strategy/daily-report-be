package com.ai.dailyReport.auth.controller;

import com.ai.dailyReport.auth.dto.SignInRequestDto;
import com.ai.dailyReport.auth.dto.SignInResponseDto;
import com.ai.dailyReport.domain.entity.User;
import com.ai.dailyReport.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/signin")
    public ResponseEntity<SignInResponseDto> signin(@Valid @RequestBody SignInRequestDto requestDto) {
        User user = authService.validateUser(requestDto.getEmail(), requestDto.getPassword());
        String jwtToken = authService.generateJwt(user);
        
        SignInResponseDto response = SignInResponseDto.builder()
            .status("SUCCESS")
            .data(SignInResponseDto.UserData.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole())
                .build())
            .token(jwtToken)
            .build();
            
        return ResponseEntity.ok(response);
    }
}