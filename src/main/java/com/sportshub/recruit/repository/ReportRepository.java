package com.sportshub.recruit.repository;

import com.sportshub.recruit.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByStatus(String status);
    List<Report> findByReporterId(Long reporterId);
    List<Report> findByReportedId(Long reportedId);
    List<Report> findByTargetTypeAndTargetId(String targetType, Long targetId);
    List<Report> findBySeverity(String severity);
    List<Report> findByCategory(String category);
}
