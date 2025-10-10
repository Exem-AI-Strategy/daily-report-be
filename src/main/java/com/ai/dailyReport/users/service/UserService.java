package com.ai.dailyReport.users.service;

import com.ai.dailyReport.domain.entity.User;
import com.ai.dailyReport.domain.repository.*;
import com.ai.dailyReport.users.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // 사용자 생성
    @Transactional
    public UserResponseDto createUser(UserCreateDto createDto) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(createDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = User.builder()
            .name(createDto.getName())
            .email(createDto.getEmail())
            .password(passwordEncoder.encode(createDto.getPassword()))
            .role(createDto.getRole())
            .team(createDto.getTeam())
            .build();
            
        User savedUser = userRepository.save(user);
        return UserResponseDto.from(savedUser);
    }
    
    // 사용자 조회 (ID)
    public UserResponseDto findById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return UserResponseDto.from(user);
    }
    
    // 사용자 조회 (이메일)
    public UserResponseDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return UserResponseDto.from(user);
    }
    
    // 모든 사용자 조회
    public List<UserResponseDto> findAllUsers() {
        return userRepository.findAll().stream()
            .map(UserResponseDto::from)
            .collect(Collectors.toList());
    }
    
    // 사용자 정보 수정
    @Transactional
    public UserResponseDto updateUser(Long userId, UserUpdateDto updateDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        if (updateDto.getName() != null) {
            user.setName(updateDto.getName());
        }
        if (updateDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }
        if (updateDto.getRole() != null) {
            user.setRole(updateDto.getRole());
        }
        if (updateDto.getTeam() != null) {
            user.setTeam(updateDto.getTeam());
        }
        
        User updatedUser = userRepository.save(user);
        return UserResponseDto.from(updatedUser);
    }
    
    // 사용자 삭제
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }
}