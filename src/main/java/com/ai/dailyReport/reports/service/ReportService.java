package com.ai.dailyReport.reports.service;

import com.ai.dailyReport.domain.entity.Report;
import com.ai.dailyReport.domain.entity.ReportMention;
import com.ai.dailyReport.domain.entity.User;
import com.ai.dailyReport.domain.repository.ReportRepository;
import com.ai.dailyReport.domain.repository.ReportMentionRepository;
import com.ai.dailyReport.domain.repository.UserRepository;
import com.ai.dailyReport.reports.dto.ReportCreateDto;
import com.ai.dailyReport.reports.dto.ReportUpdateDto;
import com.ai.dailyReport.reports.dto.ReportResponseDto;
import com.ai.dailyReport.reports.dto.ClickUpTaskDto;
import com.ai.dailyReport.reports.dto.MentionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {
    
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportMentionRepository reportMentionRepository;
    private final ClickUpApiService clickUpApiService;
    
    // 리포트 생성
    @Transactional
    public ReportResponseDto createReport(Long userId, ReportCreateDto createDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // ClickUp 링크 처리
        if (createDto.getLink() != null && !createDto.getLink().isEmpty()) {
            clickUpApiService.processClickUpLink(createDto.getLink(), user.getClickUpToken());
        }
            
        Report report = Report.builder()
            .user(user)
            .reportStartDate(createDto.getReportStartDate())
            .reportEndDate(createDto.getReportEndDate())
            .title(createDto.getTitle())
            .content(createDto.getContent())
            .link(createDto.getLink())
            .build();
            
        Report savedReport = reportRepository.save(report);

        if (createDto.getMentionedUserIds() == null) {
            log.info("No mentions provided in create (null)");
        } else if (createDto.getMentionedUserIds().isEmpty()) {
            log.info("No mentions provided in create (empty)");
        } else {
            log.info("Creating mentions for reportId={}, mentionedUserIds={}", savedReport.getReportId(), createDto.getMentionedUserIds());
            Set<Long> uniqueUserIds = new HashSet<>(createDto.getMentionedUserIds());
            List<User> mentionedUsers = userRepository.findAllById(uniqueUserIds);
            log.info("Found {} users to mention", mentionedUsers.size());
            List<ReportMention> mentionsToSave = mentionedUsers.stream()
                .filter(mentionedUser -> !reportMentionRepository.existsByReportAndMentionedUser(savedReport, mentionedUser))
                .map(mentionedUser -> ReportMention.builder()
                    .report(savedReport)
                    .mentionedUser(mentionedUser)
                    .build())
                .collect(Collectors.toList());
            if (!mentionsToSave.isEmpty()) {
                log.info("Saving {} mentions", mentionsToSave.size());
                reportMentionRepository.saveAll(mentionsToSave);
            }
        }

        return createReportResponseWithClickUpData(savedReport, userId);
    }
    
    // 리포트 조회 (ID)
    public ReportResponseDto findById(Long reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));
        
        // ClickUp 데이터 포함하여 반환
        return createReportResponseWithClickUpData(report, null);
    }

    // 뷰어 기준으로 응답 구성 (mentioner 포함 판단)
    public ReportResponseDto findById(Long reportId, Long viewerUserId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));
        return createReportResponseWithClickUpData(report, viewerUserId);
    }
    
    // 사용자의 모든 리포트 조회
    public List<ReportResponseDto> findByUserId(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        return reportRepository.findByUserOrderByReportStartDateDesc(user).stream()
            .map(r -> ReportResponseDto.from(r))
            // .map(r -> createReportResponseWithClickUpData(r, userId))
            .collect(Collectors.toList());
    }
    
    // 날짜 범위로 리포트 조회
    public List<ReportResponseDto> findByUserIdAndDateRange(
        Long userId, 
        LocalDate startDate, 
        LocalDate endDate
    ) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return reportRepository.findVisibleReports(
                user,
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX),
                Sort.by(Sort.Direction.ASC, "reportStartDate")
        ).stream()
            .map(r -> createReportResponseWithClickUpData(r, userId))
            .collect(Collectors.toList());
    }
    
    // 날짜 범위로 리포트 조회 (ClickUp 호출 제외 - 주간 조회 전용)
    public List<ReportResponseDto> findByUserIdAndDateRangeNoClickUp(
        Long userId,
        LocalDate startDate,
        LocalDate endDate
    ) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return reportRepository.findVisibleReports(
                user,
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX),
                Sort.by(Sort.Direction.ASC, "reportStartDate")
        ).stream()
            .map(r -> createReportResponseWithoutClickUpData(r, userId))
            .collect(Collectors.toList());
    }

    // 모든 리포트 조회 (관리자용)
    public List<ReportResponseDto> findAllReports() {
        return reportRepository.findAll().stream()
            .map(r -> createReportResponseWithoutClickUpData(r, null))
            .collect(Collectors.toList());
    }
    
    // 리포트 수정
    @Transactional
    public ReportResponseDto updateReport(Long reportId, Long userId, ReportUpdateDto updateDto) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));
            
        // 본인의 리포트만 수정 가능
        if (!report.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("You can only update your own reports");
        }
        
        // ClickUp 링크 처리 (링크가 변경된 경우)
        if (updateDto.getLink() != null && !updateDto.getLink().equals(report.getLink())) {
            clickUpApiService.processClickUpLink(updateDto.getLink(), report.getUser().getClickUpToken());
        }
        
        if (updateDto.getTitle() != null) {
            report.setTitle(updateDto.getTitle());
        }
        if (updateDto.getContent() != null) {
            report.setContent(updateDto.getContent());
        }
        if (updateDto.getLink() != null) {
            report.setLink(updateDto.getLink());
        }
        if (updateDto.getReportStartDate() != null) {
            report.setReportStartDate(updateDto.getReportStartDate());
        }
        if (updateDto.getReportEndDate() != null) {
            report.setReportEndDate(updateDto.getReportEndDate());
        }
        
        if (updateDto.getMentionedUserIds() == null) {
            log.info("No mentions provided in update (null) - skipping sync");
        } else {
            log.info("Syncing mentions for reportId={}, mentionedUserIds={}", report.getReportId(), updateDto.getMentionedUserIds());
            List<ReportMention> existingMentions = reportMentionRepository.findByReport(report);
            Set<Long> existingUserIds = existingMentions.stream()
                .map(m -> m.getMentionedUser().getUserId())
                .collect(Collectors.toSet());

            Set<Long> newUserIds = new HashSet<>(updateDto.getMentionedUserIds());

            Set<Long> toAdd = new HashSet<>(newUserIds);
            toAdd.removeAll(existingUserIds);

            Set<Long> toRemove = new HashSet<>(existingUserIds);
            toRemove.removeAll(newUserIds);

            if (!toRemove.isEmpty()) {
                List<User> usersToRemove = userRepository.findAllById(toRemove);
                for (User u : usersToRemove) {
                    reportMentionRepository.deleteByReportAndMentionedUser(report, u);
                }
                log.info("Removed {} mentions", toRemove.size());
            }

            if (!toAdd.isEmpty()) {
                List<User> usersToAdd = userRepository.findAllById(toAdd);
                List<ReportMention> mentionsToAdd = usersToAdd.stream()
                    .map(u -> ReportMention.builder().report(report).mentionedUser(u).build())
                    .collect(Collectors.toList());
                if (!mentionsToAdd.isEmpty()) {
                    reportMentionRepository.saveAll(mentionsToAdd);
                }
                log.info("Added {} mentions", toAdd.size());
            }
        }

        Report updatedReport = reportRepository.save(report);
        return createReportResponseWithClickUpData(updatedReport, userId);
    }
    
    // 리포트 삭제
    @Transactional
    public void deleteReport(Long reportId, Long userId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));
            
        // 본인의 리포트만 삭제 가능
        if (!report.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own reports");
        }
        
        reportRepository.deleteById(reportId);
    }
    
    // ClickUp 데이터를 포함하여 ReportResponseDto를 생성하는 헬퍼 메서드
    private ReportResponseDto createReportResponseWithClickUpData(Report report) {
        return createReportResponseWithClickUpData(report, null);
    }

    private ReportResponseDto createReportResponseWithClickUpData(Report report, Long viewerUserId) {
        ClickUpTaskDto clickUpTask = null;
        List<MentionDto> mentions = new ArrayList<>();
        
        // 링크가 있으면 ClickUp API 호출
        if (report.getLink() != null && !report.getLink().isEmpty()) {
            clickUpTask = clickUpApiService.getClickUpTaskData(report.getLink(), report.getUser().getClickUpToken());
        }

        // 언급 목록 구성
        List<ReportMention> mentionEntities = reportMentionRepository.findByReport(report);
        boolean viewerIsMentioned = false;
        boolean viewerIsAuthor = viewerUserId != null && report.getUser().getUserId().equals(viewerUserId);
        if (mentionEntities != null && !mentionEntities.isEmpty()) {
            for (ReportMention m : mentionEntities) {
                mentions.add(MentionDto.from(m));
                if (viewerUserId != null && m.getMentionedUser().getUserId().equals(viewerUserId)) {
                    viewerIsMentioned = true;
                }
            }
        }

        // 뷰어가 멘션된 사용자이거나 작성자라면, 작성자도 mentions에 포함(표시용 isMentioner=true)
        if (viewerUserId != null && (viewerIsMentioned || viewerIsAuthor)) {
            MentionDto mentioner = MentionDto.builder()
                .mentionId(null)
                .userId(report.getUser().getUserId())
                .userName(report.getUser().getName())
                .createdAt(report.getCreatedAt())
                .isMentioner(true)
                .build();
            // 중복 방지: 혹시 동일 userId가 이미 있다면 추가하지 않음
            boolean exists = mentions.stream().anyMatch(md -> md.getUserId().equals(mentioner.getUserId()));
            if (!exists) {
                mentions.add(mentioner);
            }
        }
        
        // ClickUp 데이터와 함께 ReportResponseDto 생성
        return ReportResponseDto.from(report, clickUpTask, mentions);
    }

    // ClickUp 호출 없이 ReportResponseDto를 생성 (mentions 포함)
    private ReportResponseDto createReportResponseWithoutClickUpData(Report report, Long viewerUserId) {
        List<MentionDto> mentions = new ArrayList<>();

        // 언급 목록 구성
        List<ReportMention> mentionEntities = reportMentionRepository.findByReport(report);
        boolean viewerIsMentioned = false;
        boolean viewerIsAuthor = viewerUserId != null && report.getUser().getUserId().equals(viewerUserId);
        if (mentionEntities != null && !mentionEntities.isEmpty()) {
            for (ReportMention m : mentionEntities) {
                mentions.add(MentionDto.from(m));
                if (viewerUserId != null && m.getMentionedUser().getUserId().equals(viewerUserId)) {
                    viewerIsMentioned = true;
                }
            }
        }

        // 뷰어가 멘션된 사용자이거나 작성자라면, 작성자도 mentions에 포함(표시용 isMentioner=true)
        if (viewerUserId != null && (viewerIsMentioned || viewerIsAuthor)) {
            MentionDto mentioner = MentionDto.builder()
                .mentionId(null)
                .userId(report.getUser().getUserId())
                .userName(report.getUser().getName())
                .createdAt(report.getCreatedAt())
                .isMentioner(true)
                .build();
            boolean exists = mentions.stream().anyMatch(md -> md.getUserId().equals(mentioner.getUserId()));
            if (!exists) {
                mentions.add(mentioner);
            }
        }

        return ReportResponseDto.from(report, null, mentions);
    }
}