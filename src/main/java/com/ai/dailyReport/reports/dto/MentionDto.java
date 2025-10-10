package com.ai.dailyReport.reports.dto;

import com.ai.dailyReport.domain.entity.ReportMention;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MentionDto {
    private Long mentionId;
    private Long userId;
    private String userName;
    private LocalDateTime createdAt;
    private boolean isMentioner;

    public static MentionDto from(ReportMention mention) {
        return MentionDto.builder()
            .mentionId(mention.getReportMentionId())
            .userId(mention.getMentionedUser().getUserId())
            .userName(mention.getMentionedUser().getName())
            .createdAt(mention.getCreatedAt())
            .isMentioner(false)
            .build();
    }
}


