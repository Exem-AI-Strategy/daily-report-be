package com.ai.dailyReport.reports.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ClickUpTaskDto {
    private String name;
    
    @JsonProperty("custom_id")
    private String customId;
    
    private ClickUpStatus status;
    
    @JsonProperty("date_created")
    private String dateCreated;
    
    @JsonProperty("date_updated")
    private String dateUpdated;
    
    @JsonProperty("due_date")
    private String dueDate;
    
    @JsonProperty("start_date")
    private String startDate;
    
    // due_date getter - null이면 빈 문자열 반환
    public String getDueDate() {
        return dueDate != null ? dueDate : "";
    }
    
    // start_date getter - null이면 빈 문자열 반환
    public String getStartDate() {
        return startDate != null ? startDate : "";
    }
    
    private ClickUpPriority priority;
    
    private String url;
    
    // 에러 정보 (API 호출 실패 시)
    private String error;
    private String errorMessage;
    
    @Data
    public static class ClickUpStatus {
        private String id;
        private String status;
        private String color;
        private Integer orderindex;
        private String type;
    }
    
    @Data
    public static class ClickUpPriority {
        private String color;
        private String id;
        private String orderindex;
        private String priority;
    }
    
    // 에러 정보와 함께 ClickUpTaskDto 생성
    public static ClickUpTaskDto createError(String error, String errorMessage) {
        ClickUpTaskDto dto = new ClickUpTaskDto();
        dto.setError(error);
        dto.setErrorMessage(errorMessage);
        return dto;
    }
}
