package com.ai.dailyReport.reports.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportCreateDto {
    @NotNull(message = "Report start date is required")
    private LocalDateTime reportStartDate;
    
    @NotNull(message = "Report end date is required")
    private LocalDateTime reportEndDate;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String content;
}