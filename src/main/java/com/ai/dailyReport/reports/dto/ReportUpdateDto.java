package com.ai.dailyReport.reports.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportUpdateDto {
    private LocalDateTime reportStartDate;
    private LocalDateTime reportEndDate;
    
    @NotBlank(message = "Title cannot be empty")
    private String title;
    
    private String content;
}