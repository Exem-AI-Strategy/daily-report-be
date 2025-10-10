package com.ai.dailyReport.reports.controller;


import com.ai.dailyReport.auth.service.AuthService;
import com.ai.dailyReport.common.exception.UnauthorizedException;
import com.ai.dailyReport.common.response.ApiResponse;
import com.ai.dailyReport.reports.dto.ReportCreateDto;
import com.ai.dailyReport.reports.dto.ReportResponseDto;
import com.ai.dailyReport.reports.dto.ReportUpdateDto;
import com.ai.dailyReport.reports.service.ReportService;
import com.ai.dailyReport.users.service.UserService;

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
  private final AuthService authService;
  private final UserService userService;

	// Report 생성 (본문 userId 없으면 토큰 사용자로 대체)
	@PostMapping
	public ResponseEntity<ApiResponse<ReportResponseDto>> create(
		@Valid @RequestBody ReportCreateDto dto,
		Authentication authentication
	) {
		String email = authentication.getName();
    Long userId = userService.findByEmail(email).getUserId();

		System.out.println("dto.mentionedUserIds=" + dto.getMentionedUserIds());
		ReportResponseDto created = reportService.createReport(userId, dto);
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success("Report가 성공적으로 생성되었습니다.", created));
	}

  // 주간 조회 (관리자용)
  @GetMapping("/weekly/{userId}")
  public ResponseEntity<ApiResponse<WeeklyPayload>> findWeeklyReportsByUserId(
    @PathVariable Long userId,
    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
    Authentication authentication
  )
  {
    String role = userService.findByEmail(authentication.getName()).getRole();
    if (!role.equals("ADMIN")) {
      throw new UnauthorizedException("Unauthorized: 관리자 권한이 필요합니다.");
    }
    List<ReportResponseDto> reports = reportService.findByUserIdAndDateRange(userId, startDate, endDate);
    WeeklyPayload payload = new WeeklyPayload(startDate, endDate, reports);
    return ResponseEntity.ok(ApiResponse.success("주간 Report 조회에 성공했습니다.", payload));
  }

  // 주간 조회 ?startDate=yyyy-MM-dd&endDate=yyyy-MM-dd
	@GetMapping("/weekly")
	public ResponseEntity<ApiResponse<WeeklyPayload>> findWeeklyReports(
		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
		Authentication authentication
	) {
		Long userId = authService.getCurrentUserId(authentication);

		List<ReportResponseDto> reports = reportService.findByUserIdAndDateRange(userId, startDate, endDate);
		WeeklyPayload payload = new WeeklyPayload(startDate, endDate, reports);

		return ResponseEntity.ok(ApiResponse.success("주간 Report 조회에 성공했습니다.", payload));
	}

  // 일간 조회 (관리자용)
  @GetMapping("/daily/{userId}")
  public ResponseEntity<ApiResponse<DailyPayload>> findDailyReportsByUserId(
    @PathVariable Long userId,
    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
    Authentication authentication
  ) {
    String role = userService.findByEmail(authentication.getName()).getRole();
    if (!role.equals("ADMIN")) {
      throw new UnauthorizedException("Unauthorized: 관리자 권한이 필요합니다.");
    }
    List<ReportResponseDto> reports = reportService.findByUserIdAndDateRange(userId, date, date);
    return ResponseEntity.ok(ApiResponse.success("일간 Report 조회에 성공했습니다.", new DailyPayload(reports)));
  }

	// 일간 조회 ?date=yyyy-MM-dd
	@GetMapping("/daily")
	public ResponseEntity<ApiResponse<DailyPayload>> findDailyReports(
		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
		Authentication authentication
	) {
		Long userId = authService.getCurrentUserId(authentication);

		List<ReportResponseDto> reports = reportService.findByUserIdAndDateRange(userId, date, date);
		return ResponseEntity.ok(ApiResponse.success("일간 Report 조회에 성공했습니다.", new DailyPayload(reports)));
	}

	// 단건 조회
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<ReportResponseDto>> findOne(
		@PathVariable Long id,
		Authentication authentication
	) {
		Long userId = authService.getCurrentUserId(authentication);
		ReportResponseDto report = reportService.findById(id, userId);
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
		Long userId = authService.getCurrentUserId(authentication);
		System.out.println("update.mentionedUserIds=" + dto.getMentionedUserIds());
		ReportResponseDto updated = reportService.updateReport(id, userId, dto);
		return ResponseEntity.ok(ApiResponse.success("Report가 성공적으로 수정되었습니다.", updated));
	}

	// 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> remove(
		@PathVariable Long id,
		Authentication authentication
	) {
		Long userId = authService.getCurrentUserId(authentication);
		reportService.deleteReport(id, userId);
		return ResponseEntity.ok(ApiResponse.success("Report가 성공적으로 삭제되었습니다.", null));
	}

	// 내부 응답 payload (주간/일간)
	public record WeeklyPayload(LocalDate weekStartDate, LocalDate weekEndDate, List<ReportResponseDto> reports) {}
	public record DailyPayload(List<ReportResponseDto> reports) {}
}