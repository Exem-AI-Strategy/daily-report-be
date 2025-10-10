package com.ai.dailyReport.users.dto;

import com.ai.dailyReport.domain.entity.User;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UserResponseDto {
    private Long userId;
    private String name;
    private String email;
    private String role;
    private String clickUpToken;
    private String team;
    
    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()  
            .userId(user.getUserId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole())
            .clickUpToken(user.getClickUpToken())
            .team(user.getTeam())
            .build();
    }
}