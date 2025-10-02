package com.ai.dailyReport.reports.service;

import com.ai.dailyReport.domain.entity.Report;
import com.ai.dailyReport.domain.entity.User;
import com.ai.dailyReport.domain.repository.ReportRepository;
import com.ai.dailyReport.domain.repository.UserRepository;
import com.ai.dailyReport.reports.dto.ReportCreateDto;
import com.ai.dailyReport.reports.dto.ReportUpdateDto;
import com.ai.dailyReport.reports.dto.ReportResponseDto;
import com.ai.dailyReport.reports.service.ClickUpApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {
    
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ClickUpApiService clickUpApiService;
    
    // 리포트 생성
    @Transactional
    public ReportResponseDto createReport(Long userId, ReportCreateDto createDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // ClickUp 링크 처리
        if (createDto.getLink() != null && !createDto.getLink().isEmpty()) {
            clickUpApiService.processClickUpLink(createDto.getLink());
        }
            
        Report report = Report.builder()
            .user(user)
            .reportStartDate(createDto.getReportStartDate())
            .reportEndDate(createDto.getReportEndDate())
            .title(createDto.getTitle())
            .content(createDto.getContent())
            .link(createDto.getLink())
            .build();
            
        Report savedReport = reportRepository.save(report);
        return ReportResponseDto.from(savedReport);
    }
    
    // 리포트 조회 (ID)
    public ReportResponseDto findById(Long reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));
        return ReportResponseDto.from(report);
    }
    
    // 사용자의 모든 리포트 조회
    public List<ReportResponseDto> findByUserId(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        return reportRepository.findByUserOrderByReportStartDateDesc(user).stream()
            .map(ReportResponseDto::from)
            .collect(Collectors.toList());
    }
    
    // 날짜 범위로 리포트 조회
    public List<ReportResponseDto> findByUserIdAndDateRange(
        Long userId, 
        LocalDate startDate, 
        LocalDate endDate
    ) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return reportRepository.findByUserAndReportStartDateBetween(user, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)).stream()
            .map(ReportResponseDto::from)
            .collect(Collectors.toList());
    }
    
    // 모든 리포트 조회 (관리자용)
    public List<ReportResponseDto> findAllReports() {
        return reportRepository.findAll().stream()
            .map(ReportResponseDto::from)
            .collect(Collectors.toList());
    }
    
    // 리포트 수정
    @Transactional
    public ReportResponseDto updateReport(Long reportId, Long userId, ReportUpdateDto updateDto) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));
            
        // 본인의 리포트만 수정 가능
        if (!report.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("You can only update your own reports");
        }
        
        // ClickUp 링크 처리 (링크가 변경된 경우)
        if (updateDto.getLink() != null && !updateDto.getLink().equals(report.getLink())) {
            clickUpApiService.processClickUpLink(updateDto.getLink());
        }
        
        if (updateDto.getTitle() != null) {
            report.setTitle(updateDto.getTitle());
        }
        if (updateDto.getContent() != null) {
            report.setContent(updateDto.getContent());
        }
        if (updateDto.getLink() != null) {
            report.setLink(updateDto.getLink());
        }
        if (updateDto.getReportStartDate() != null) {
            report.setReportStartDate(updateDto.getReportStartDate());
        }
        if (updateDto.getReportEndDate() != null) {
            report.setReportEndDate(updateDto.getReportEndDate());
        }
        
        Report updatedReport = reportRepository.save(report);
        return ReportResponseDto.from(updatedReport);
    }
    
    // 리포트 삭제
    @Transactional
    public void deleteReport(Long reportId, Long userId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));
            
        // 본인의 리포트만 삭제 가능
        if (!report.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own reports");
        }
        
        reportRepository.deleteById(reportId);
    }
}