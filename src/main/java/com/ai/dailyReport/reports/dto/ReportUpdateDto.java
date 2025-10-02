package com.ai.dailyReport.reports.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class ReportUpdateDto {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportStartDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportEndDate;
    
    @NotBlank(message = "Title cannot be empty")
    private String title;
    
    private String content;
    private String link;
    private Long userId;
    private Long reportId;
}