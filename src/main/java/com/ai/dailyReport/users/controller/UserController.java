package com.ai.dailyReport.users.controller;

import com.ai.dailyReport.auth.jwt.JwtTokenProvider;
import com.ai.dailyReport.auth.service.AuthService;
import com.ai.dailyReport.common.exception.UnauthorizedException;
import com.ai.dailyReport.domain.entity.User;
import com.ai.dailyReport.users.dto.UserCreateDto;
import com.ai.dailyReport.users.dto.UserResponseDto;
import com.ai.dailyReport.users.dto.UserUpdateDto;
import com.ai.dailyReport.users.service.UserService;
import lombok.RequiredArgsConstructor;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    /** @Todo 사용자 아이디 파라미터 제거 -> 토큰에서 추출 */
    private final UserService userService;
    private final AuthService authService;

    // 사용자 생성
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto createDto) {
        UserResponseDto createdUser = userService.createUser(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    // 특정 사용자 정보 조회 (권한 확인)
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(
        @PathVariable Long userId,
        Authentication authentication
    ) {
        String currentUserEmail = authentication.getName();
        UserResponseDto currentUser = userService.findByEmail(currentUserEmail);
        
        // admin이거나 본인 정보 조회인 경우만 허용
        if (!currentUser.getRole().equals("ADMIN") && !currentUser.getUserId().equals(userId)) {
            throw new UnauthorizedException("Unauthorized: 다른 사용자 정보를 조회할 권한이 없습니다.");
        }
        
        UserResponseDto user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }
    
    // 모든 사용자 조회
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.findAllUsers();
        // 1순위: 관리자 우선 (ADMIN이 먼저)
        // 2순위: 이름 가나다순
        users.sort(Comparator
        .comparing(UserResponseDto::getRole, (role1, role2) -> {
            if ("ADMIN".equals(role1) && !"ADMIN".equals(role2)) return -1;
            if (!"ADMIN".equals(role1) && "ADMIN".equals(role2)) return 1;
            return 0;
        })
        .thenComparing(UserResponseDto::getName)
    );

        
        return ResponseEntity.ok(users);
    }
    
    // 사용자 정보 수정
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(
        @PathVariable Long userId, 
        @Valid @RequestBody UserUpdateDto updateDto
    ) {
        UserResponseDto updatedUser = userService.updateUser(userId, updateDto);
        return ResponseEntity.ok(updatedUser);
    }
    
    // 사용자 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}