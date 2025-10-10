package com.ai.dailyReport.reports.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;

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

    // 언급 사용자 ID 목록 (전체 교체 방식)
    @JsonAlias({"mentioned_user_ids"})
    private List<Long> mentionedUserIds;
}