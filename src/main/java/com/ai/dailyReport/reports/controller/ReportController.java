package com.ai.dailyReport.reports.controller;

import com.ai.dailyReport.reports.dto.ReportCreateDto;
import com.ai.dailyReport.reports.dto.ReportResponseDto;
import com.ai.dailyReport.reports.dto.ReportUpdateDto;
import com.ai.dailyReport.reports.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportService reportService;
    
    // 리포트 생성
    @PostMapping
    public ResponseEntity<ReportResponseDto> createReport(
        @RequestParam Long userId,
        @Valid @RequestBody ReportCreateDto createDto
    ) {
        ReportResponseDto createdReport = reportService.createReport(userId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReport);
    }
    
    // 리포트 조회 (ID)
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportResponseDto> getReportById(@PathVariable Long reportId) {
        ReportResponseDto report = reportService.findById(reportId);
        return ResponseEntity.ok(report);
    }
    
    // 사용자의 모든 리포트 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReportResponseDto>> getReportsByUserId(@PathVariable Long userId) {
        List<ReportResponseDto> reports = reportService.findByUserId(userId);
        return ResponseEntity.ok(reports);
    }
    
    // 날짜 범위로 리포트 조회
    @GetMapping("/user/{userId}/range")
    public ResponseEntity<List<ReportResponseDto>> getReportsByDateRange(
        @PathVariable Long userId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<ReportResponseDto> reports = reportService.findByUserIdAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(reports);
    }
    
    // 모든 리포트 조회 (관리자용)
    @GetMapping
    public ResponseEntity<List<ReportResponseDto>> getAllReports() {
        List<ReportResponseDto> reports = reportService.findAllReports();
        return ResponseEntity.ok(reports);
    }
    
    // 리포트 수정
    @PutMapping("/{reportId}")
    public ResponseEntity<ReportResponseDto> updateReport(
        @PathVariable Long reportId,
        @RequestParam Long userId,
        @Valid @RequestBody ReportUpdateDto updateDto
    ) {
        ReportResponseDto updatedReport = reportService.updateReport(reportId, userId, updateDto);
        return ResponseEntity.ok(updatedReport);
    }
    
    // 리포트 삭제
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(
        @PathVariable Long reportId,
        @RequestParam Long userId
    ) {
        reportService.deleteReport(reportId, userId);
        return ResponseEntity.noContent().build();
    }
}