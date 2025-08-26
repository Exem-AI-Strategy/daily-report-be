package com.ai.dailyReport.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "name", length = 30, nullable = false)
    private String name;
    
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;
    
    @Column(name = "password", length = 72, nullable = false)
    private String password;
    
    @Column(name = "role", length = 10, nullable = false)
    private String role = "USER"; // 기본값 설정
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;
    
    // enum 대신 상수 사용 (데이터베이스 CHECK 제약조건과 일치)
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";
    
    // 편의 메서드
    public boolean isAdmin() {
        return ROLE_ADMIN.equals(this.role);
    }
    
    public boolean isUser() {
        return ROLE_USER.equals(this.role);
    }
}