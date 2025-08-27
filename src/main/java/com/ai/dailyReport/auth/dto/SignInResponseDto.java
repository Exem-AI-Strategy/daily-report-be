package com.ai.dailyReport.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignInResponseDto {
    private String status;
    private UserData data;
    private String token;
    
    @Data
    @Builder
    public static class UserData {
        private Long userId;
        private String email;
        private String role;
        private String name;
    }
}