package com.ai.dailyReport.users.dto;

import com.ai.dailyReport.domain.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDto {
    private Long userId;
    private String name;
    private String email;
    private String role;
    
    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()  
            .userId(user.getUserId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole())
            .build();
    }
}