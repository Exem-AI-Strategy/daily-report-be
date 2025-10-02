package com.ai.dailyReport.reports.dto;

import com.ai.dailyReport.domain.entity.Report;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportResponseDto {
    private Long reportId;
    private Long userId;
    private String userName;
    private LocalDateTime reportStartDate;
    private LocalDateTime reportEndDate;
    private String title;
    private String content;
    private String link;
    private ClickUpTaskDto clickUpTask;  // ClickUp 데이터 추가
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static ReportResponseDto from(Report report) {
        return ReportResponseDto.builder()
            .reportId(report.getReportId())
            .userId(report.getUser().getUserId())
            .userName(report.getUser().getName())
            .reportStartDate(report.getReportStartDate())
            .reportEndDate(report.getReportEndDate())
            .title(report.getTitle())
            .content(report.getContent())
            .link(report.getLink())
            .clickUpTask(null)  // 기본값은 null, ReportService에서 설정
            .createdAt(report.getCreatedAt())
            .updatedAt(report.getUpdatedAt())
            .build();
    }
    
    // ClickUp 데이터와 함께 생성하는 메서드
    public static ReportResponseDto from(Report report, ClickUpTaskDto clickUpTask) {
        return ReportResponseDto.builder()
            .reportId(report.getReportId())
            .userId(report.getUser().getUserId())
            .userName(report.getUser().getName())
            .reportStartDate(report.getReportStartDate())
            .reportEndDate(report.getReportEndDate())
            .title(report.getTitle())
            .content(report.getContent())
            .link(report.getLink())
            .clickUpTask(clickUpTask)
            .createdAt(report.getCreatedAt())
            .updatedAt(report.getUpdatedAt())
            .build();
    }
}