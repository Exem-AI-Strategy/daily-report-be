package com.ai.dailyReport.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "report_mention",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"report_id", "mentioned_user_id"})
    }
)
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportMention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_mention_id")
    private Long reportMentionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentioned_user_id", nullable = false)
    private User mentionedUser;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ReportMention(Report report, User mentionedUser) {
        this.report = report;
        this.mentionedUser = mentionedUser;
    }
}


