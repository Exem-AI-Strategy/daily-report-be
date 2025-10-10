package com.ai.dailyReport.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ai.dailyReport.domain.entity.Report;
import com.ai.dailyReport.domain.entity.User;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByUserOrderByReportStartDateDesc(User user);
    List<Report> findByUserAndReportStartDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
    List<Report> findByUserAndReportStartDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate, Sort sort);

    @Query("""
    select distinct r from Report r
    left join ReportMention rm on rm.report = r
    where (r.user = :user or rm.mentionedUser = :user)
      and r.reportStartDate between :start and :end
    """)
    List<Report> findVisibleReports(
        @Param("user") User user,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        Sort sort
    );
}
