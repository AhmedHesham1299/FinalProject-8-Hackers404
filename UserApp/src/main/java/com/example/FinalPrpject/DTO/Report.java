package com.example.FinalPrpject.DTO;

public class Report {
    private Long reporterUserId;
    private Long reportedUserId;
    private String reason;

    public Report() {
    }

    public Report(Long reporterUserId, String reason, Long reportedUserId) {
        this.reporterUserId = reporterUserId;
        this.reason = reason;
        this.reportedUserId = reportedUserId;
    }

    public Long getReporterUserId() {
        return reporterUserId;
    }

    public void setReporterUserId(Long reporterUserId) {
        this.reporterUserId = reporterUserId;
    }

    public Long getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(Long reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

