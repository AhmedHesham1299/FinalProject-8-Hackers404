package com.example.FinalPrpject.services;

import com.example.FinalPrpject.models.Report;
import com.example.FinalPrpject.models.ReportStatus;
import com.example.FinalPrpject.repositories.ReportRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public Report createReport(Report report) {
        report.setReportDate(LocalDateTime.now());
        report.setStatus(ReportStatus.PENDING);
        return reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    public List<Report> getReportsByStatus(ReportStatus status) {
        return reportRepository.findByStatus(status);
    }

    public Report updateStatus(Long id, ReportStatus status) {
        Report report = reportRepository.findById(id).orElseThrow();
        report.setStatus(status);
        return reportRepository.save(report);
    }

    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

}
