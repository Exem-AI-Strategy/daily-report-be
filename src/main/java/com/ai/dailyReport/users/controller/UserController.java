package com.ai.dailyReport.users.controller;

import com.ai.dailyReport.users.dto.UserCreateDto;
import com.ai.dailyReport.users.dto.UserResponseDto;
import com.ai.dailyReport.users.dto.UserUpdateDto;
import com.ai.dailyReport.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    // 사용자 생성
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto createDto) {
        UserResponseDto createdUser = userService.createUser(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    // 사용자 조회 (ID)
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        UserResponseDto user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }
    
    // 모든 사용자 조회
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.findAllUsers();
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