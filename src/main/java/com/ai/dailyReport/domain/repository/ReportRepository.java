package com.ai.dailyReport.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ai.dailyReport.domain.entity.Report;
import com.ai.dailyReport.domain.entity.User;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByUserOrderByReportStartDateDesc(User user);
    List<Report> findByUserAndReportStartDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
}
