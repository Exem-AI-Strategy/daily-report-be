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
            .createdAt(report.getCreatedAt())
            .updatedAt(report.getUpdatedAt())
            .build();
    }
}