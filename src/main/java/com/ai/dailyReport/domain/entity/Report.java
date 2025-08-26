package com.ai.dailyReport.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "report")
@Data
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "report_start_date", nullable = false)
    private LocalDateTime reportStartDate;
    
    @Column(name = "report_end_date", nullable = false)
    private LocalDateTime reportEndDate;
    
    @Column(name = "title", length = 100, nullable = false)
    private String title;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;
    
    // 편의 메서드
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(reportStartDate) && now.isBefore(reportEndDate);
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(reportEndDate);
    }
    
    public boolean isUpcoming() {
        return LocalDateTime.now().isBefore(reportStartDate);
    }
}