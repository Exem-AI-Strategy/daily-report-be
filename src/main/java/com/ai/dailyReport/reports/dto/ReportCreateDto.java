package com.ai.dailyReport.reports.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class ReportCreateDto {
    @NotNull(message = "Report start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportStartDate;
    
    @NotNull(message = "Report end date is required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportEndDate;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String content;
    
    private String link;
}