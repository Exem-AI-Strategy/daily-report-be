package com.ai.dailyReport.clickup.controller;

import com.ai.dailyReport.clickup.dto.ClickUpTaskRequestDto;
import com.ai.dailyReport.common.response.ApiResponse;
import com.ai.dailyReport.reports.dto.ClickUpTaskDto;
import com.ai.dailyReport.reports.service.ClickUpApiService;
import com.ai.dailyReport.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clickup")
@RequiredArgsConstructor
public class ClickUpController {
    
    private final ClickUpApiService clickUpApiService;
    private final UserService userService;
    
    @PostMapping("/task")
    public ResponseEntity<ApiResponse<ClickUpTaskDto>> getClickUpTask(
            @RequestBody ClickUpTaskRequestDto request,
            Authentication authentication
    ) {
        try {
            // 현재 사용자 정보 가져오기
            String email = authentication.getName();
            var user = userService.findByEmail(email);
            
            // 사용자의 ClickUp 토큰으로 API 호출
            ClickUpTaskDto clickUpTask = clickUpApiService.getClickUpTaskData(request.getUrl(), user.getClickUpToken());
            
            if (clickUpTask != null) {
                return ResponseEntity.ok(ApiResponse.success("ClickUp 태스크 조회에 성공했습니다.", clickUpTask));
            } else {
                return ResponseEntity.ok(ApiResponse.fail("ClickUp 태스크를 찾을 수 없습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("ClickUp 태스크 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}
