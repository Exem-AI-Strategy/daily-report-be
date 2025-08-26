package com.ai.dailyReport.reports.controller;

import com.ai.dailyReport.common.exception.UnauthorizedException;
import com.ai.dailyReport.common.response.ApiResponse;
import com.ai.dailyReport.domain.entity.User;
import com.ai.dailyReport.domain.repository.UserRepository;
import com.ai.dailyReport.reports.dto.ReportCreateDto;
import com.ai.dailyReport.reports.dto.ReportResponseDto;
import com.ai.dailyReport.reports.dto.ReportUpdateDto;
import com.ai.dailyReport.reports.service.ReportService;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;
	private final UserRepository userRepository;

	// Report 생성 (본문 userId 없으면 토큰 사용자로 대체)
	@PostMapping
	public ResponseEntity<ApiResponse<ReportResponseDto>> create(
		@Valid @RequestBody ReportCreateDto dto,
		Authentication authentication
	) {
		Long userId = getCurrentUserId(authentication);

		ReportResponseDto created = reportService.createReport(userId, dto);
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success("Report가 성공적으로 생성되었습니다.", created));
	}

	// 주간 조회 ?startDate=yyyy-MM-dd&endDate=yyyy-MM-dd
	@GetMapping("/weekly")
	public ResponseEntity<ApiResponse<WeeklyPayload>> findWeeklyReports(
		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
		Authentication authentication
	) {
		Long userId = getCurrentUserId(authentication);

		List<ReportResponseDto> reports = reportService.findByUserIdAndDateRange(userId, startDate, endDate);
		WeeklyPayload payload = new WeeklyPayload(startDate, endDate, reports);

		return ResponseEntity.ok(ApiResponse.success("주간 Report 조회에 성공했습니다.", payload));
	}

	// 일간 조회 ?date=yyyy-MM-dd
	@GetMapping("/daily")
	public ResponseEntity<ApiResponse<DailyPayload>> findDailyReports(
		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
		Authentication authentication
	) {
		Long userId = getCurrentUserId(authentication);

		List<ReportResponseDto> reports = reportService.findByUserIdAndDateRange(userId, date, date);
		return ResponseEntity.ok(ApiResponse.success("일간 Report 조회에 성공했습니다.", new DailyPayload(reports)));
	}

	// 단건 조회
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<ReportResponseDto>> findOne(
		@PathVariable Long id,
		Authentication authentication
	) {
		getCurrentUserId(authentication); // 필요 시 소유자 검증은 서비스에서 수행
		ReportResponseDto report = reportService.findById(id);
		return ResponseEntity.ok(ApiResponse.success("Report 조회에 성공했습니다.", report));
	}

	// 수정 (부분 수정)
  // @Todo userId, reportId 요청 본문에서 제거하기
	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponse<ReportResponseDto>> update(
		@PathVariable Long id,
		@Valid @RequestBody ReportUpdateDto dto,
		Authentication authentication
	) {
		Long userId = getCurrentUserId(authentication);
		ReportResponseDto updated = reportService.updateReport(id, userId, dto);
		return ResponseEntity.ok(ApiResponse.success("Report가 성공적으로 수정되었습니다.", updated));
	}

	// 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> remove(
		@PathVariable Long id,
		Authentication authentication
	) {
		Long userId = getCurrentUserId(authentication);
		reportService.deleteReport(id, userId);
		return ResponseEntity.ok(ApiResponse.success("Report가 성공적으로 삭제되었습니다.", null));
	}

	private Long getCurrentUserId(Authentication authentication) {
    if (authentication == null) {
      throw new UnauthorizedException("Unauthorized");
  }
		String email = authentication.getName(); // CustomUserDetailsService에서 username=email로 설정
    System.out.println("email: " + email);
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new RuntimeException("User not found"));
		return user.getUserId();
	}

	// 내부 응답 payload (주간/일간)
	public record WeeklyPayload(LocalDate weekStartDate, LocalDate weekEndDate, List<ReportResponseDto> reports) {}
	public record DailyPayload(List<ReportResponseDto> reports) {}
}