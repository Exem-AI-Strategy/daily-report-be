package com.ai.dailyReport.domain.repository;

import com.ai.dailyReport.domain.entity.Report;
import com.ai.dailyReport.domain.entity.ReportMention;
import com.ai.dailyReport.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportMentionRepository extends JpaRepository<ReportMention, Long> {
    List<ReportMention> findByReport(Report report);
    List<ReportMention> findByReportReportId(Long reportId);
    boolean existsByReportAndMentionedUser(Report report, User mentionedUser);
    void deleteByReportAndMentionedUser(Report report, User mentionedUser);
}


