package com.sportshub.recruit.service;

import com.sportshub.recruit.domain.Report;
import com.sportshub.recruit.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {
    private final ReportRepository reportRepository;

    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    public Report findById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
    }

    public List<Report> findByStatus(String status) {
        return reportRepository.findByStatus(status);
    }

    public List<Report> findByReporterId(Long reporterId) {
        return reportRepository.findByReporterId(reporterId);
    }

    public List<Report> findByReportedId(Long reportedId) {
        return reportRepository.findByReportedId(reportedId);
    }

    public List<Report> findBySeverity(String severity) {
        return reportRepository.findBySeverity(severity);
    }

    @Transactional
    public Report create(Report report) {
        return reportRepository.save(report);
    }

    @Transactional
    public Report update(Long id, Report updates) {
        Report existing = findById(id);

        if (updates.getStatus() != null) existing.setStatus(updates.getStatus());
        if (updates.getSeverity() != null) existing.setSeverity(updates.getSeverity());
        if (updates.getAssignedAdminId() != null) existing.setAssignedAdminId(updates.getAssignedAdminId());
        if (updates.getResolution() != null) existing.setResolution(updates.getResolution());
        if (updates.getResolvedAt() != null) existing.setResolvedAt(updates.getResolvedAt());

        return reportRepository.save(existing);
    }

    @Transactional
    public Report resolve(Long id, String resolution) {
        Report report = findById(id);
        report.setStatus("RESOLVED");
        report.setResolution(resolution);
        report.setResolvedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }

    @Transactional
    public Report reject(Long id, String reason) {
        Report report = findById(id);
        report.setStatus("REJECTED");
        report.setResolution(reason);
        report.setResolvedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }

    @Transactional
    public void delete(Long id) {
        reportRepository.deleteById(id);
    }
}
