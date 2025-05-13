package com.example.FinalPrpject.controllers;

import com.example.FinalPrpject.models.Report;
import com.example.FinalPrpject.models.ReportStatus;
import com.example.FinalPrpject.services.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<Report> create(@RequestBody Report report) {
        return ResponseEntity.ok(reportService.createReport(report));
    }

    @GetMapping
    public ResponseEntity<List<Report>> getAll() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> getById(@PathVariable Long id) {
        return reportService.getReportById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status")
    public ResponseEntity<List<Report>> getByStatus(@RequestParam ReportStatus status) {
        return ResponseEntity.ok(reportService.getReportsByStatus(status));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Report> updateStatus(@PathVariable Long id, @RequestParam ReportStatus status) {
        return ResponseEntity.ok(reportService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

}
