package com.sportshub.recruit.web;

import com.sportshub.recruit.domain.Report;
import com.sportshub.recruit.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping
    public List<Report> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) Long reporterId,
            @RequestParam(required = false) Long reportedId
    ) {
        if (status != null) {
            return reportService.findByStatus(status);
        }
        if (severity != null) {
            return reportService.findBySeverity(severity);
        }
        if (reporterId != null) {
            return reportService.findByReporterId(reporterId);
        }
        if (reportedId != null) {
            return reportService.findByReportedId(reportedId);
        }
        return reportService.findAll();
    }

    @GetMapping("/{id}")
    public Report get(@PathVariable Long id) {
        return reportService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Report create(@RequestBody Report report) {
        return reportService.create(report);
    }

    @PatchMapping("/{id}")
    public Report update(@PathVariable Long id, @RequestBody Report updates) {
        return reportService.update(id, updates);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reportService.delete(id);
    }

    @PostMapping("/{id}/resolve")
    public Report resolve(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String action = payload.getOrDefault("action", "");
        String note = payload.getOrDefault("note", "");
        String resolution = String.format("Action: %s, Note: %s", action, note);
        return reportService.resolve(id, resolution);
    }

    @PostMapping("/{id}/reject")
    public Report reject(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String reason = payload.getOrDefault("reason", "");
        return reportService.reject(id, reason);
    }
}
